/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.olap;

import java.util.Objects;

import mondrian.olap.Member;



/**
 *
 * @author mahfoud
 */
public class EAB_Measure {
    
    Member mondrianMeasure;
    EAB_Level level;
    
    //
    String name;
    String uniqueName;
    
    /**
     *
     * @param arg_mondrianMeasure
     * @param arg_level
     */
    public EAB_Measure(Member arg_mondrianMeasure, EAB_Level arg_level) {
        this.mondrianMeasure    = arg_mondrianMeasure;
        this.level              = arg_level;
        this.name               = this.mondrianMeasure.getName();
        this.uniqueName         = this.mondrianMeasure.getUniqueName();
    }
    
    
    
    /**
     *
     * @return
     */
    public EAB_Cube getCube() {
        return this.getLevel().getCube();
    }

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
    public EAB_Hierarchy getHierarchy() {
        return this.getLevel().getHierarchy();
    } 
    
    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     *
     * @return
     */
    public String getUniqueName() {
        return this.uniqueName;
    }
 
    /**
     *
     * @return
     */
    public Member getMondrianMeasure() {
        return this.mondrianMeasure;
    }

    /**
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.mondrianMeasure);
        hash = 29 * hash + Objects.hashCode(this.level);
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.uniqueName);
        return hash;
    }
    
    /**
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        EAB_Measure m_tmp   = (EAB_Measure)obj;
        return this.getMondrianMeasure().equals(m_tmp.getMondrianMeasure());
    }
    
}
