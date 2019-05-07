package fr.univ_tours.li.mdjedaini.ideb.olap.query;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Level;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;

/**
 *
 * @author Salim IGUE
 */
public class SelectionFragment extends Fragment {

    /**
     * 
     */
    EAB_Level level;
    
    EAB_Member value;
   
    /**
     * 
     * @param arg_q
     * @param arg_value 
     */
    public SelectionFragment(QueryTriplet arg_q, EAB_Member arg_value) {
        this.q      = arg_q;
        this.level  = arg_value.getLevel();
        this.value  = arg_value;
    }
    
    /**
     * 
     * @param arg_q
     * @param arg_dimensionName
     * @param arg_hierarchyName
     * @param arg_levelName
     * @param arg_selectionValue 
     */
    public SelectionFragment(QueryTriplet arg_q, String arg_dimensionName, String arg_hierarchyName, String arg_levelName, String arg_selectionValue) {
        this.q      = arg_q;
        this.level  = this.getQuery().getCube().cubeLevelList.get("[" + arg_dimensionName + "." + arg_hierarchyName + "].[" + arg_levelName + "]");
        this.value  = this.getQuery().getCube().cubeMemberList.get(this.level).get(arg_selectionValue);
    }

    /**
     *
     * @return
     */
    public String getDimensionName() {
        return this.level.getHierarchy().getDimension().getUniqueName();
    }

    /**
     *
     * @return
     */
    public String getHierarchyName() {
        return this.level.getHierarchy().getUniqueName();
    }

    /**
     *
     * @return
     */
    public String getLevelName() {
        return this.level.getName();
    }
//
//    public String getSelectionValue() {
//        return selectionValue;
//    }
    
    /**
     *
     * @return
     */
    public EAB_Level getLevel() {
        return this.level;
    }
    
    /**
     *
     * @return
     */
    public EAB_Member getMemberValue() {
        return this.value;
    }
    
    /**
     * 
     * @return 
     */
    public String toMDX() {
        String result   = "";
        
//        result  += this.benchmarkEngine.getBenchmarkData().getInternalCube().getLevel(hierarchyName, levelName).toString();
        result  += "[" + this.value + "]";
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public String toString() {
        String result   = "";
        
        
        
        result  += this.level.getUniqueName() + "=\"" + this.value.getUniqueName() + "\"";
        
        return result;
    }

    /**
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        if(null != this.getMemberValue()) {
            return this.getMemberValue().hashCode();
        }
        return 0;
    }

    /**
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        
        final SelectionFragment other = (SelectionFragment) obj;
        
        // they are equal if they are not null and if they are the same
        return (this.getMemberValue() != null) && (other.getMemberValue() != null) && (this.getMemberValue().equals(other.getMemberValue()));
    }
    
    
}
