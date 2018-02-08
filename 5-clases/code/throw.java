public Object pop() { 
	Object obj; 
	
	if (size == 0)  throw new EmptyStackException();
	
	obj = objectAt(size - 1); 
	setObjectAt(size - 1, null); 
	size--; 
	return obj;
}
