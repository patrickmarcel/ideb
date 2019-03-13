/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.olap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import mondrian.olap.Level;
import mondrian.olap.Member;


/**
 *
 * @author mahfoud
 */
public class EAB_Level {
    
    Level mondrianLevel;
    
    EAB_Hierarchy hierarchy;
    
    String name;
    String uniqueName;

    /**
     * 
     * @param arg_mondrianLevel 
     * @param arg_hierarchy 
     */
    public EAB_Level(Level arg_mondrianLevel, EAB_Hierarchy arg_hierarchy) {
        this.mondrianLevel  = arg_mondrianLevel;
        this.hierarchy      = arg_hierarchy;
        
        //this.mondrianLevel.
        this.name           = arg_mondrianLevel.getName();
        this.uniqueName     = arg_hierarchy.getUniqueName() + ".[" + this.name + "]";
    }

    /**
     * 
     * @return 
     */
    public Level getMondrianLevel() {
        return mondrianLevel;
    }

    /**
     * Checks whether the level is All level or not.
     * It is done by checking if the member contained in the hierarchy is the All member.
     * @return 
     */
    public Boolean isAllLevel() {
        return this.mondrianLevel.isAll();
    }
    
    /**
     * todo gerer les cas ou le level n'a pas de children...
     * @return 
     */
    public EAB_Level getChildLevel() {
        return this.getCube().getLevelByMondrianLevel(this.mondrianLevel.getChildLevel());
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Level getParentLevel() {
        return this.getCube().getLevelByMondrianLevel(this.mondrianLevel.getParentLevel());
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Member getRandomMember() {
        Random randomizer   = new Random();
        Integer index       = randomizer.nextInt(this.getLevelMembers().size());
        return this.getLevelMembers().get(index);
    }
    
    /**
     * 
     * @param arg_level
     * @return 
     */
    public boolean isChildOf(EAB_Level arg_level) {
        Level l_tmp = this.mondrianLevel.getParentLevel();
        
        while(l_tmp != null) {
            if(l_tmp.equals(arg_level.mondrianLevel)) {
                return true;
            }
            l_tmp   = l_tmp.getParentLevel();
        }
        
        return false;
    }
    
    /**
     * 
     * @param arg_level
     * @return 
     */
    public boolean isDirectChildOf(EAB_Level arg_level) {
        return !this.isAllLevel() && this.mondrianLevel.getParentLevel().equals(arg_level.mondrianLevel);
    }
    
    /**
     * Retrieves the depth of the current level within its hierarchy.
     * @return depth of the current level
     */
    public Integer getLevelDepth() {
        return this.getMondrianLevel().getDepth();
    }
    
    /**
     * 
     * @return 
     */
    public List<EAB_Member> getLevelMembers() {
        List<EAB_Member> result  = new ArrayList<>();
        
        List<Member> memberList = this.getCube().getSchemaReader().getLevelMembers(mondrianLevel, null);
        
        for(Member m_tmp : memberList) {
            result.add(this.getCube().getMemberByMondrianMember(m_tmp));
        }
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Hierarchy getHierarchy() {        
        return hierarchy;
    }
    
    /**
     *
     * @return
     */
    public EAB_Cube getCube() {
        return this.getHierarchy().getDimension().getCube();
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
        return uniqueName;
    }

    /**
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.uniqueName);
        return hash;
    }

    /**
     * 
     * @param arg_o
     * @return 
     */
    @Override
    public boolean equals(Object arg_o) {
        EAB_Level other = (EAB_Level)arg_o;
        return this.mondrianLevel.equals(other.getMondrianLevel());
    }
    
    /**
     * Two levels are neighbors if one is a direct child of another.
     * @param arg_level
     * @return 
     */
    public Boolean isNeighborOf(EAB_Level arg_level) {
        return (this.isDirectChildOf(arg_level) || arg_level.isDirectChildOf(this));
    }
    
}
