package builder;

import olap.CellSet;
import org.olap4j.Cell;
import org.olap4j.CellSetAxis;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;

import java.util.*;

public class AncestorOrDescendantProxyBuilder implements ProxyBuilder {
    @Override
    public Map<List<Integer>, Set<List<Integer>>> computeProxyMatrix(CellSet cellSetNew, CellSet cellSetOld) { //TODO coordinates are currently (col, row) change it to (row, col)
        Map<List<Integer>, Set<List<Integer>>> proxy = new HashMap<>();

        List<CellSetAxis> axesNew = cellSetNew.getAxes();
        List<CellSetAxis> axesOld = cellSetOld.getAxes();
        int nbOfCellsNew = cellSetNew.getNbOfCells();
        int nbOfCellsOld = cellSetOld.getNbOfCells();

        // Build member map for new cell set
        List<Map<Hierarchy, Member>> memberMapListNew = new ArrayList<>();
        for (int ordinal = 0; ordinal < nbOfCellsNew; ordinal++) {
            Cell cell = cellSetNew.getCell(ordinal);
            List<Integer> coordinates = cell.getCoordinateList();
            Map<Hierarchy, Member> memberMap = new HashMap<>();
            for (int axisIndex = 0; axisIndex < axesNew.size(); axisIndex++) {
                CellSetAxis axis = axesNew.get(axisIndex);
                for (Member member : axis.getPositions().get(coordinates.get(axisIndex)).getMembers()) {
                    memberMap.putIfAbsent(member.getHierarchy(), member);
                }
            }
            memberMapListNew.add(memberMap);
        }
        // Build member map for new cell set
        List<Map<Hierarchy, Member>> memberMapListOld = new ArrayList<>();
        for (int ordinal = 0; ordinal < nbOfCellsOld; ordinal++) {
            Cell cell = cellSetOld.getCell(ordinal);
            List<Integer> coordinates = cell.getCoordinateList();
            Map<Hierarchy, Member> memberMap = new HashMap<>();
            for (int axisIndex = 0; axisIndex < axesOld.size(); axisIndex++) {
                CellSetAxis axis = axesOld.get(axisIndex);
                for (Member member : axis.getPositions().get(coordinates.get(axisIndex)).getMembers()) {
                    memberMap.putIfAbsent(member.getHierarchy(), member);
                }
            }
            memberMapListOld.add(memberMap);
        }

        // Build proxies
        for (int ordinalNew = 0; ordinalNew < memberMapListNew.size(); ordinalNew++) {
            for (int ordinalOld = 0; ordinalOld < memberMapListOld.size(); ordinalOld++) {
                Cell cellNew = cellSetNew.getCell(ordinalNew);
                Cell cellOld = cellSetOld.getCell(ordinalOld);
                if (!cellNew.isNull() && !cellNew.isError() && !cellNew.isEmpty() && !cellOld.isNull() && !cellOld.isError() && !cellOld.isEmpty()) {

                    Map<Hierarchy, Member> memberMapNew = memberMapListNew.get(ordinalNew);
                    Map<Hierarchy, Member> memberMapOld = memberMapListOld.get(ordinalOld);

                    if (memberMapNew.keySet().equals(memberMapOld.keySet())) {
                        boolean isProxy = true;
                        for (Hierarchy hierarchy : memberMapNew.keySet()) {
                            Member memberNew = memberMapNew.get(hierarchy);
                            Member memberOld = memberMapOld.get(hierarchy);

                            if (!memberNew.getAncestorMembers().contains(memberOld) && !memberOld.getAncestorMembers().contains(memberNew)) {
                                isProxy = false;
                                break;
                            }
                        }
                        if (isProxy) {
                            List<Integer> coordinatesNew = cellSetNew.ordinalToCoordinates(ordinalNew);
                            List<Integer> coordinatesOld = cellSetOld.ordinalToCoordinates(ordinalOld);

                            if (proxy.containsKey(coordinatesNew)) {
                                proxy.get(coordinatesNew).add(coordinatesOld);
                            } else {
                                Set<List<Integer>> coordinatesSet = new HashSet<>();
                                coordinatesSet.add(coordinatesOld);
                                proxy.put(coordinatesNew, coordinatesSet);
                            }
                        }
                    }
                }
            }
        }
        return proxy;
    }
}
