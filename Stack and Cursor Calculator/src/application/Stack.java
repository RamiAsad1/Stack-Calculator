package application;

public class Stack {
	private static final int MAX_SIZE = 1000;
	private CursorNode[] cursorArray;
	private int top;
	
	public Stack() { 
		cursorArray = new CursorNode[MAX_SIZE];
		
		for(int i=0;i<MAX_SIZE;i++) 
			cursorArray[i] = new CursorNode(null , i + 1);
		
		cursorArray[MAX_SIZE - 1].next = 0;
		top = 0;
	}

	public void push(Object data) {
		if (top < MAX_SIZE) {
			int pos = cursorAlloc();
			cursorArray[pos].element = data;
			cursorArray[pos].next = top;
			top = pos;
		} 
	}

	public Object pop() {
		if (!isEmpty()) {
			Object data = cursorArray[top].element;
			int tmp = top;
			top = cursorArray[top].next;
			cursorFree(tmp);
			return data;
		} else {
			return null;
		}
	}

	public Object peek() {
		if (!isEmpty()) {
			return cursorArray[top].element;
		} else {
			return null;
		}
	}

	public boolean isEmpty() {
		return top == 0;
	}

	public int cursorAlloc() {
		int p = cursorArray[0].next;
		cursorArray[0].next = cursorArray[p].next;
		return p;
	}
	private void cursorFree(int p) {
		cursorArray[p].element = null; // free the content
		cursorArray[p].next = cursorArray[0].next;
		cursorArray[0].next = p;
	}
}
