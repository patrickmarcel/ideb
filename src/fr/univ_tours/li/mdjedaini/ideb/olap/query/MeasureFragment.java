package fr.univ_tours.li.mdjedaini.ideb.olap.query;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Measure;

/**
 *
 * @author Salim IGUE
 */
public class MeasureFragment extends Fragment implements java.io.Serializable {

    /**
     * 
     */
    EAB_Measure measure;

    /**
     * 
     * @param arg_q
     * @param arg_measureName 
     */
    public MeasureFragment(QueryTriplet arg_q, String arg_measureName) {
        this.q          = arg_q;
        this.measure    = this.getQuery().getCube().getMeasureByName(arg_measureName);
    }
    
    /**
     * 
     * @param arg_q
     * @param arg_measure 
     */
    public MeasureFragment(QueryTriplet arg_q, EAB_Measure arg_measure) {
        this.q          = arg_q;
        this.measure    = arg_measure;
    }
    
    /**
     * 
     * @return 
     */
    public String getMeasureName() {
        return this.measure.getUniqueName();
    }

    /**
     *
     * @return
     */
    public EAB_Measure getMeasure() {
        return measure;
    }

    /**
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.measure.hashCode();
        return hash;
    }
    
    
    /**
     * 
     * @param arg_o
     * @return 
     */
    @Override
    public boolean equals(Object arg_o) {
        MeasureFragment arg_mf  = (MeasureFragment)arg_o;
        return (this.getMeasure().equals(arg_mf.getMeasure()));
    }
    
    
    
    /**
     * 
     * @return 
     */
    public String toString() {
        return this.measure.getName();
    }
}
