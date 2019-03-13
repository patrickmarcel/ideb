/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.olap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import mondrian.olap.Cube;
import mondrian.olap.Dimension;
import mondrian.olap.Hierarchy;
import mondrian.olap.Level;
import mondrian.olap.Member;
import mondrian.olap.SchemaReader;
import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;


/**
 *
 * @author mahfoud
 */
public class EAB_Cube {
    
    //
    BenchmarkEngine be;
    
    //
    Cube mondrianCube;
    
    String name;
    
    // structure du cube

    /**
     *
     */
    public List<EAB_Measure> cubeMeasureListOrdered;    // important for classifying measures (ex: default measure is first)

    /**
     *
     */
    public Map<String, EAB_Measure> cubeMeasureList;

    /**
     *
     */
    public Map<String, EAB_Dimension> cubeDimensionList;

    /**
     *
     */
    public Map<String, EAB_Hierarchy> cubeHierarchyList;

    /**
     *
     */
    public Map<String, EAB_Level> cubeLevelList;

    /**
     *
     */
    public Map<EAB_Level, Map<String, EAB_Member>> cubeMemberList;
    
    // mapping mondrian | my own classes

    /**
     *
     */
    public Map<Member, EAB_Measure> measureMapping;

    /**
     *
     */
    public Map<Dimension, EAB_Dimension> dimensionMapping;

    /**
     *
     */
    public Map<Hierarchy, EAB_Hierarchy> hierarchyMapping;

    /**
     *
     */
    public Map<Level, EAB_Level> levelMapping;

    /**
     *
     */
    public Map<Member, EAB_Member> memberMapping;
    
    /**
     *
     */
    public Map<EAB_Hierarchy, EAB_MemberTree> cubeMemberTree;
    
    //
//    HashMap<String, Member> measureList;
//    HashMap<String, Dimension> dimensionList;
//    HashMap<String, Hierarchy> hierarchyList;
//    HashMap<String, Level> levelList;
    Map<String, EAB_Member> memberList;
    
    /**
     * 
     * @param arg_be
     * @param arg_mondrianCube 
     */
    public EAB_Cube(BenchmarkEngine arg_be, Cube arg_mondrianCube) {
        this.be                 = arg_be;
        
        this.mondrianCube       = arg_mondrianCube;
        
        this.name               = this.mondrianCube.getName();
        
        this.cubeMeasureListOrdered = new ArrayList<>();
        this.cubeMeasureList    = new HashMap<>();
        this.cubeDimensionList  = new HashMap<>();
        this.cubeHierarchyList  = new HashMap<>();
        this.cubeLevelList      = new HashMap<>();
        this.cubeMemberList     = new HashMap<>();
        
        this.measureMapping     = new HashMap<>();
        this.dimensionMapping   = new HashMap<>();
        this.hierarchyMapping   = new HashMap<>();
        this.levelMapping       = new HashMap<>();
        
        this.memberMapping      = new HashMap<>();
        this.cubeMemberTree     = new HashMap<>();
        
        this.memberList         = new HashMap<>();
    }
    
    /**
     * 
     */
    public void loadDataFromMondrianCube() {
        this.loadCubeStructure();
        this.loadCubeData();
    }

    /**
     * 
     */
    public void loadCubeStructure() {
        SchemaReader sr = this.getSchemaReader();
        
        for(Dimension d : this.mondrianCube.getDimensions()) {
//            System.out.println("Name: " + d.getName() + " - unique Name: " + d.getUniqueName() + " - Caption: " + d.getCaption() + " - Qualified Name: " + d.getQualifiedName());
            
            // @todo etudier l'alternative d.getUniqueName()
            EAB_Dimension eab_d = new EAB_Dimension(d, this);
                
            // deal with measures
            if (d.toString().equals("[Measures]")) {
                for(Hierarchy h : d.getHierarchies()) {
                    EAB_Hierarchy measureHierarchy  = new EAB_Hierarchy(h, eab_d);
//                    this.hierarchyMapping.put(h,);
//                    System.out.println("Name: " + h.getName() + " - unique Name: " + h.getUniqueName() + " - Caption: " + h.getCaption() + " - Qualified Name: " + h.getQualifiedName());
                    for(Level l : h.getLevels()) {
                        EAB_Level measureLevel  = new EAB_Level(l, measureHierarchy);
//                        System.out.println("Name: " + l.getName() + " - unique Name: " + l.getUniqueName() + " - Caption: " + l.getCaption() + " - Qualified Name: " + l.getQualifiedName());
                        Iterator<Member> it_lm    = this.getSchemaReader().getLevelMembers(l, true).iterator();
                        while(it_lm.hasNext()) {
                            Member currentMeasure   = it_lm.next();
//                            System.out.println("Name: " + currentMeasure.getName() + " - unique Name: " + currentMeasure.getUniqueName() + " - Caption: " + currentMeasure.getCaption() + " - Qualified Name: " + currentMeasure.getQualifiedName());
                            EAB_Measure m_tmp   = new EAB_Measure(currentMeasure, measureLevel);
                            this.cubeMeasureListOrdered.add(m_tmp);
                            this.cubeMeasureList.put(m_tmp.getName(), m_tmp);
                            this.measureMapping.put(currentMeasure, m_tmp);
                        }
                    }
                }
            } else {
                
                // mapping is only included for hierarchies different than MEASURES
                this.addDimension(eab_d);
                this.dimensionMapping.put(d, eab_d);
        
                for(Hierarchy h : d.getHierarchies()) {
//                    System.out.println("Name: " + h.getName() + " - unique Name: " + h.getUniqueName() + " - Caption: " + h.getCaption() + " - Qualified Name: " + h.getQualifiedName());
                    EAB_Hierarchy eab_h = new EAB_Hierarchy(h, eab_d);
                    this.addHierarchy(eab_h);
                    this.hierarchyMapping.put(h, eab_h);
                
                    for(Level l : h.getLevels()) {
                        EAB_Level eab_l = new EAB_Level(l, eab_h);
                        this.addLevel(eab_l);
                        this.levelMapping.put(l, eab_l);
                    }
                }
            }
        }

    }
    
    /**
     * For each hierarchy, we load members as a tree
     */
    public void loadCubeData() {
        SchemaReader sr = this.getSchemaReader();
        
        // I first load all the members as a mapping
        for(String l_name : this.cubeLevelList.keySet()) {
            EAB_Level l_tmp = this.cubeLevelList.get(l_name);
            
            Map<String, EAB_Member> memberList  = new HashMap<>();
            
            for(Member m_tmp : sr.getLevelMembers(l_tmp.getMondrianLevel(), false)) {
                EAB_Member eab_tmp  = new EAB_Member(m_tmp, l_tmp);
                memberList.put(m_tmp.getName(), eab_tmp);
                this.memberMapping.put(m_tmp, eab_tmp);
                this.memberList.put(m_tmp.getUniqueName(), eab_tmp);
            }
            
            this.cubeMemberList.put(l_tmp, memberList);
            
        }
        
//        for(String h_name : this.cubeHierarchyList.keySet()) {
//            EAB_Hierarchy h_tmp     = this.getHierarchyByName(h_name);
//            EAB_MemberTree eab_mt   = new EAB_MemberTree(h_tmp);
//            this.cubeMemberTree.put(h_tmp, eab_mt);
//        }
    }
    
    /**
     *
     * @return
     */
    public SchemaReader getSchemaReader() {
        return this.mondrianCube.getSchemaReader(null).withLocus();
    }
    
    /**
     * 
     * @return 
     */
    public Cube getMondrianCube() {
        return this.mondrianCube;
    }
    
    /**
     * 
     * @param arg_dim 
     */
    public void addDimension(EAB_Dimension arg_dim) {
        this.cubeDimensionList.put(arg_dim.getUniqueName(), arg_dim);
    }
    
    /**
     * 
     * @param arg_hierarchy 
     */
    public void addHierarchy(EAB_Hierarchy arg_hierarchy) {
        this.cubeHierarchyList.put(arg_hierarchy.getUniqueName(), arg_hierarchy);
    }
    
    /**
     * 
     * @param arg_level 
     */
    public void addLevel(EAB_Level arg_level) {
        this.cubeLevelList.put(arg_level.getUniqueName(), arg_level);
    }
    
    /**
     * Retrieves the list of measures.
     * @return 
     */
    public List<EAB_Measure> getMeasureList() {
        return new ArrayList<>(this.cubeMeasureList.values());
    }
    
    /**
     * 
     * @param arg_measureName
     * @return 
     */
    public EAB_Measure getMeasureByName(String arg_measureName) {
        return this.cubeMeasureList.get(arg_measureName);
    }
    
    /**
     * Get default cube measure.
     * Default measure is the measure that is first mentioned in the cube schema.
     * @return 
     */
    public EAB_Measure getDefaultMeasure() {
        return this.cubeMeasureListOrdered.get(0);
    }
    
    /**
     * 
     * @param arg_dimName
     * @return 
     */
    public EAB_Dimension getDimensionByName(String arg_dimName) {
        return this.cubeDimensionList.get(arg_dimName);
    }
    
    /**
     * 
     * @param arg_hierarchyName
     * @return 
     */
    public EAB_Hierarchy getHierarchyByName(String arg_hierarchyName) {
        return this.cubeHierarchyList.get(arg_hierarchyName);
    }
    
    /**
     * 
     * @param arg_hierarchy
     * @return 
     */
    public EAB_Hierarchy getHierarchyByMondrianHierarchy(Hierarchy arg_hierarchy) {
        return this.hierarchyMapping.get(arg_hierarchy);
    }
    
    /**
     * Retrieves the level of the cube matching this simple name.
     * The name provided as an argument should be given without the level ancestors,
     * meaning that it must not contain the hierarchy, dimension, ...
     * @param arg_levelName
     * @return 
     */
    public EAB_Level getLevelByAtomicName(String arg_levelName) {
        
        for(String levelFullName : this.cubeLevelList.keySet()) {
            
            // if the level name matches the full name
            if(levelFullName.contains("[" + arg_levelName + "]")) {
                return this.getLevelByName(levelFullName);
            }
            
        }
        
        return null;
    }
    
    /**
     * 
     * @param arg_levelName
     * @return 
     */
    public EAB_Level getLevelByName(String arg_levelName) {
        return this.cubeLevelList.get(arg_levelName);
    }
    
    /**
     * Retrieves the set of levels for the current cube.
     * @return 
     */
    public Set<EAB_Level> getLevelList() {
        return new HashSet<>(this.cubeLevelList.values());
    }
    
    /**
     * 
     * @param arg_level
     * @return 
     */
    public EAB_Level getLevelByMondrianLevel(Level arg_level) {
        return this.levelMapping.get(arg_level);
    }
    
    /**
     * 
     * @param arg_member
     * @return 
     */
    public EAB_Member getMemberByMondrianMember(Member arg_member) {
        return this.memberMapping.get(arg_member);
    }
    
    /**
     * 
     * @param arg_name
     * @return 
     */
    public EAB_Member getMemberByName(String arg_name) {
        return this.memberList.get(arg_name);
    }
    
    /**
     * 
     * @return 
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns the total number of levels in the cube.
     * @return number of levels in current cube
     */
    public Integer getNumberOfLevels() {
        Integer result  = 0;
        
        for(EAB_Hierarchy h_tmp : this.getHierarchyList()) {
            result  += h_tmp.getNumberOfLevels();
        }
        
        return result;
    }
    
    /**
     * Returns the measure object corresponding to the mondrian member.
     * @param arg_member
     * @return 
     */
    public EAB_Measure getMeasureByMondrianMember(Member arg_member) {
        return this.measureMapping.get(arg_member);
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Measure getRandomMeasure() {
        Random generator            = new Random();
        List<EAB_Measure> m_list    = new ArrayList<>(this.cubeMeasureList.values());
        return m_list.get(generator.nextInt(m_list.size()));
    }
    
    /**
     * Provides a random hierarchy from the cube.
     * todo possibilites d'ameliorations...
     * @return 
     */
    public EAB_Hierarchy getRandomHierarchy() {
        return this.getRandomLevel().getHierarchy();
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Level getRandomLevel() {
        Random generator            = new Random();
        List<EAB_Level> l_list      = new ArrayList<>(this.cubeLevelList.values());
        return l_list.get(generator.nextInt(l_list.size()));
    }
    
    /**
     * This function is to be optimized...
     * @param arg_hierarchy
     * @return 
     */
    public EAB_Level getRandomLevelByHierarchy(EAB_Hierarchy arg_hierarchy) {
        return arg_hierarchy.getRandomLevel();
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Member getRandomMember() {
        Random generator            = new Random();
        List<EAB_Member> m_list     = new ArrayList<>(this.memberMapping.values());
        return m_list.get(generator.nextInt(m_list.size()));
    }
    
    /**
     * 
     * @param arg_l
     * @return 
     */
    public EAB_Member getRandomMemberByLevel(EAB_Level arg_l) {
        return arg_l.getRandomMember();
//        Random generator            = new Random();
//        List<EAB_Member> m_list     = new ArrayList<>(this.memberMapping.values());
//        return m_list.get(generator.nextInt(m_list.size()));
    }
    
    /**
     * 
     * @param arg_h
     * @return 
     */
    public EAB_Member getRandomMemberByHierarchy(EAB_Hierarchy arg_h) {
        Random generator            = new Random();
        List<EAB_Member> m_list     = new ArrayList<>(this.memberMapping.values());
        return m_list.get(generator.nextInt(m_list.size()));
    }
        
    /**
     * 
     * @param arg_includeMeasure
     * @return 
     */
    public Set<String> getHierarchyNameList(boolean arg_includeMeasure) {
        HashSet<String> result  = new HashSet<String>(this.cubeHierarchyList.keySet());
        if(arg_includeMeasure) {
            result.add("[Measure]");
        }
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public Set<EAB_Hierarchy> getHierarchyList() {
        return new HashSet<>(this.cubeHierarchyList.values());
    }
    
    /**
     * 
     * @return 
     */
    public BenchmarkEngine getBencharkEngine() {
        return this.be;
    }
    
    /**
     * 
     * @param arg_o
     * @return 
     */
    @Override
    public boolean equals(Object arg_o) {
        EAB_Cube arg_cube   = (EAB_Cube)arg_o;
        return this.mondrianCube.equals(arg_cube.mondrianCube);
    }
    
}
