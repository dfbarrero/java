public void writeList() { 
	PrintWriter out = null; 
	try { 
		System.out.println("Entering"); 
		out = new PrintWriter(new FileWriter("Out.txt")); 
		for (int i = 0; i < SIZE; i++) 
			out.println("At:"+i+"="+vector.elementAt(i)); 
		} catch (ArrayIndexOutOfBoundsException e) { 
			System.err.println("Invalid index: " +  e.getMessage()); 
		} catch (IOException e) { 
			System.err.println("IO error:"+e.getMessage()); 
		} finally { 
			if (out != null) { 
				System.out.println("Closing PrintWriter"); 
				out.close(); 
			} else { 
				System.out.println("PrintWriter not open"); 
			} 
		} 
}
