public void writeList() throws IOException { 
	PrintWriter out = new PrintWriter(new FileWriter("OutFile.txt")); 
	
	for (int i = 0; i < SIZE; i++) { 
		out.println("Value at: " + i + " = " + vector.elementAt(i)); 
	}

	out.close(); 
}
