package ParseMIB;

import java.util.ArrayList;
import java.util.List;

public class MyTreeNode<T>{
    private T data = null;
    private List<MyTreeNode<T>> children = new ArrayList<MyTreeNode<T>>();
    private MyTreeNode<T> parent = null;

    public MyTreeNode(T data) {
        this.data = data;
    }

    public void addChild(MyTreeNode child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void addChild(T data) {
        MyTreeNode<T> newChild = new MyTreeNode<>(data);
        newChild.setParent(this);
        children.add(newChild);
    }

    public void addChildren(List<MyTreeNode<T>> children) {
        for(MyTreeNode t : children) {
            t.setParent(this);
        }
        this.children.addAll(children);
    }

    public List<MyTreeNode<T>> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void setParent(MyTreeNode<T> parent) {
        this.parent = parent;
    }

    public MyTreeNode<T> getParent() {
        return parent;
    }

    public boolean isLeaf() {
        if (this.children.size() == 0 )
        {
            return true;
        } else {
            return false;
        }
    }


}