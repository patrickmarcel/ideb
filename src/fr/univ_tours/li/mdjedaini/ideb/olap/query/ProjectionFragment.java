package fr.univ_tours.li.mdjedaini.ideb.olap.query;

import java.util.Objects;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Level;

/**
 *
 * @author Salim IGUE
 */
public class ProjectionFragment extends Fragment implements java.io.Serializable {

    // 
    private EAB_Level projectionLevel;
    
    /**
     *
     * @param arg_q
     * @param arg_level
     */
    public ProjectionFragment(QueryTriplet arg_q, EAB_Level arg_level) {
        super();
        this.q                  = arg_q;
        this.projectionLevel    = arg_level;
    }
    
    /**
     * 
     * @param arg_q
     * @param arg_dimensionName
     * @param arg_hierarchyName
     * @param arg_levelName 
     */
    public ProjectionFragment(QueryTriplet arg_q, String arg_dimensionName, String arg_hierarchyName, String arg_levelName) {
        this.q                  = arg_q;
        this.projectionLevel    = this.getQuery().getCube().cubeLevelList.get("[" + arg_dimensionName + "." + arg_hierarchyName + "].[" + arg_levelName + "]");
    }

    /**
     * 
     * @return 
     */
    public EAB_Level getLevel() {
        return this.projectionLevel;
    }
    
    /**
     * 
     * @return 
     */
    public String toString() {
        return this.projectionLevel.getUniqueName();
    }
    
    /**
     * 
     * @param arg_o
     * @return 
     */
    public boolean equals(Object arg_o) {
        ProjectionFragment arg_pf   = (ProjectionFragment)arg_o;
        return (this.projectionLevel.equals(arg_pf.projectionLevel));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.projectionLevel);
        return hash;
    }
    
    /**
     * 
     * @return 
     */
    public String getDimensionName() {
        return this.projectionLevel.getHierarchy().getDimension().getUniqueName();
    }

    /**
     * 
     * @return 
     */
    public String getHierarchyName() {
        return this.projectionLevel.getHierarchy().getUniqueName();
    }

    /**
     * 
     * @return 
     */
    public String getLevelName() {
        return this.projectionLevel.getUniqueName();
    }
}
