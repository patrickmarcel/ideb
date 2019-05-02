package result;

import java.util.ArrayList;
import java.util.List;

/**
 * Table header, used for building graphical table. Don't forget to call {@link HeaderTree#updateSpanAndTrimChildren()}
 * after building the tree to compute span value and remove empty children list.
 */
public class HeaderTree {

    /**
     * Header name or value
     */
    private String name;

    /**
     * Header span which is how many rows (for column header) or columns (for row header) should the header
     * cover
     */
    private int span;

    /**
     * List of sub header
     */
    private List<HeaderTree> children;

    public HeaderTree(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public void updateSpanAndTrimChildren() {
        if (children == null || children.isEmpty()) {
            span = 1;
            children = null;
        } else {
            span = 0;
            for (HeaderTree child : children) {
                child.updateSpanAndTrimChildren();
                span += child.span;
            }
        }
    }

    /**
     * Search for a direct child with the given name from th children list
     *
     * @param childrenName The child name
     * @return The child if found or null if not
     */
    public HeaderTree getChildNamed(String childrenName) {
        if (childrenName == null) return null;
        for (HeaderTree child : children) {
            if (childrenName.equals(child.name)) return child;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getSpan() {
        return span;
    }

    public List<HeaderTree> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "HeaderTree{" +
                "name='" + name + '\'' +
                ", span=" + span +
                ", children=" + children +
                '}';
    }
}
