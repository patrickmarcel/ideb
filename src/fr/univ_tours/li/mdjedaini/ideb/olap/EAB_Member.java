/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.olap;

import java.util.HashSet;
import java.util.Set;

import mondrian.olap.Member;
import mondrian.olap.SchemaReader;


/**
 *
 * @author mahfoud
 */
public class EAB_Member {
    
    //
    Member mondrianMember;
    
    //
    EAB_Level level;
    
    //
    String name;
    String uniqueName;
    
    /**
     * 
     * @param arg_mondrianMember
     * @param arg_level 
     */
    public EAB_Member(Member arg_mondrianMember, EAB_Level arg_level) {
        this.mondrianMember = arg_mondrianMember;
        this.level          = arg_level;
        this.name           = arg_mondrianMember.getName();
        this.uniqueName     = arg_mondrianMember.getUniqueName();
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
        
        hash = 17 * hash + this.uniqueName.hashCode();
        return hash;
    }

    /**
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        final EAB_Member other = (EAB_Member) obj;
        
        return this.getUniqueName().equals(other.getUniqueName());
    }
    
    
    
    /**
     * 
     * @return 
     */
    public Set<EAB_Member> getChildren() {
        Set<EAB_Member> result = new HashSet<>();
        
        for(Member m_tmp : this.getCube().getSchemaReader().getMemberChildren(mondrianMember)) {
            result.add(this.getCube().memberMapping.get(m_tmp));
        }
            
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isAll() {
        return this.mondrianMember.isAll();
    }
    
    /**
     * 
     * @param arg_level
     * @return 
     */
    public Set<EAB_Member> getChildrenAtLevel(EAB_Level arg_level) {
        Set<EAB_Member> result = new HashSet<>();
        
        for(Member m_tmp : this.getCube().getSchemaReader().getMemberChildren(mondrianMember)) {
            EAB_Member eab_m    = this.getCube().memberMapping.get(m_tmp);
            if(eab_m.getLevel().equals(arg_level)) {
                result.add(eab_m);
            } else {
                result.addAll(eab_m.getChildrenAtLevel(arg_level));
            }
        }
            
        return result;
    }
    
    /**
     * 
     * @param arg_member
     * @return 
     */
    public boolean isChildOf(EAB_Member arg_member) {
        Member m_tmp    = this.mondrianMember.getParentMember();
        
        while(m_tmp != null) {
            if(m_tmp.equals(arg_member.mondrianMember)) {
                return true;
            }
            m_tmp   = m_tmp.getParentMember();
        }
        
        return false;
    }
    
    /**
     * 
     * @param arg_member
     * @return 
     */
    public boolean isDirectChildOf(EAB_Member arg_member) {
        return !this.isAll() && this.mondrianMember.getParentMember().equals(arg_member.mondrianMember);
    }
    
    /**
     * 
     * @param arg_member
     * @return 
     */
    public boolean isBrotherOf(EAB_Member arg_member) {
        // get the schema reader to query the schema
        SchemaReader sr     = this.getCube().getSchemaReader();
        
        Member m1_parent    = this.mondrianMember.getParentMember();
        Member m2_parent    = arg_member.mondrianMember.getParentMember();
        
        if(m1_parent != null && m2_parent != null && m1_parent == m2_parent) {
            return true;
        }
        
        return false;
    }

    /**
     * 
     * @return 
     */
    public Set<EAB_Member> getSiblings() {
       Set<EAB_Member> res  = new HashSet<>();
       
       if(this.isAll()) {
           return res;
       }
       
       // @todo verifier si getchildren retourne les enfants directs ou alors
       // les enfants a n'importe quel niveau...
       res.addAll(this.getParentMember().getChildren());
       
       return res;
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Member getParentMember() {
        Member mondrianParent   = this.mondrianMember.getParentMember();
        return this.getCube().getMemberByMondrianMember(mondrianParent);
    }
    
    /**
     * 
     * @return 
     */
    public Set<EAB_Member> getParents() {
        HashSet<EAB_Member> res = new HashSet<>();
        
        if(this.isAll()) {
            return res;
        }
        
        EAB_Member currentParent    = this.getParentMember();
        while(!currentParent.isAll()) {
            res.add(currentParent);
            currentParent   = currentParent.getParentMember();
        }
        
        res.add(currentParent);
        
        return res;
    }
    
    /**
     * 
     * @return 
     */
    public Member getMondrianMember() {
        return mondrianMember;
    }

    /**
     * 
     * @return 
     */
    public EAB_Level getLevel() {
        return level;
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Cube getCube() {
        return this.getLevel().getHierarchy().getDimension().getCube();
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        return this.name;
    }
    
}
