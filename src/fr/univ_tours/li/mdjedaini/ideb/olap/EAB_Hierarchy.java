/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.olap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import mondrian.olap.Hierarchy;
import mondrian.olap.Level;

/**
 *
 * @author mahfoud
 */
public class EAB_Hierarchy {
    
    Hierarchy mondrianHierarchy;
    
    EAB_Dimension dimension;
    
    String name;
    String uniqueName;
    
    /**
     * 
     * @param arg_mondrianHierarchy
     * @param arg_dimension 
     */
    public EAB_Hierarchy(Hierarchy arg_mondrianHierarchy, EAB_Dimension arg_dimension) {
        this.mondrianHierarchy  = arg_mondrianHierarchy;        
        this.dimension          = arg_dimension;
        
        List<String> items = Arrays.asList(arg_mondrianHierarchy.getName().split("\\s*\\.\\s*"));
        
        // if its a measure, the size is 1
        if(items.size() == 1) {
            this.name       = items.get(0);
        } else {
            this.name       = items.get(1);
        }
        
        this.uniqueName = "[" + arg_mondrianHierarchy.getName() + "]";
    }
    
    /**
     * 
     * @return 
     */
    public String getName() {
        return this.name;
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
    public Hierarchy getMondrianHierarchy() {
        return this.mondrianHierarchy;
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Level getAllLevel() {
        return this.getAllMember().getLevel();
    }
    
    /**
     * 
     * @return 
     */
    public List<EAB_Level> getLevels() {
        List<EAB_Level> result  = new ArrayList<>();
        
        for(Level l_tmp : this.mondrianHierarchy.getLevels()) {
            result.add(this.getCube().getLevelByMondrianLevel(l_tmp));
        }
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Member getRandomMember() {
        return this.getRandomLevel().getRandomMember();
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Level getRandomLevel() {
//        System.out.println("Random level pour la hierarchie " + this.uniqueName);
        Random randomizer   = new Random();
        Integer index       = randomizer.nextInt(this.getNumberOfLevels());
        return this.getLevels().get(index);
    }
    
    /**
     * 
     * @param arg_level
     * @return 
     */
    public EAB_Level getRandomLevelAboveLevel(EAB_Level arg_level) {
        EAB_Level l_tmp = this.getRandomLevel();

        // we return the level if it is above
        if(l_tmp.equals(arg_level) || arg_level.isChildOf(l_tmp)) {
            return l_tmp;
        } else {
            return this.getRandomLevelAboveLevel(arg_level);
        }
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Level getMostDetailedLevel() {
        Level l = this.mondrianHierarchy.getLevels()[this.mondrianHierarchy.getLevels().length-1];
        return this.getCube().getLevelByMondrianLevel(l);
    }
    
    /**
     *
     * @return
     */
    public EAB_Member getAllMember() {
        return this.getCube().memberMapping.get(this.mondrianHierarchy.getAllMember());
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Dimension getDimension() {
        return this.dimension;
    }
    
    /**
     * 
     * @return 
     */
    public Integer getNumberOfLevels() {
        return this.mondrianHierarchy.getLevels().length;
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Cube getCube() {
        return this.getDimension().getCube();
    }
    
    /**
     * 
     * @param arg_o
     * @return 
     */
    @Override
    public boolean equals(Object arg_o) {
        EAB_Hierarchy arg_h = (EAB_Hierarchy)arg_o;
        return this.mondrianHierarchy.equals(arg_h.mondrianHierarchy);
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        return this.uniqueName;
    }
    
}
