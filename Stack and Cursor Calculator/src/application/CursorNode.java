package application;

public class CursorNode {// this is the node class for the cursor array
	public Object element;
	public int next;

	public CursorNode(Object element) {
		this(element, 0);
	} 

	public CursorNode(Object element, int next) {
		this.element = element;
		this.next = next;
	}

}
