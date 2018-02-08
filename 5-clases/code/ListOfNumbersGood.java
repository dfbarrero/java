try { 
	System.out.println("Entered try statement"); 
	out = new PrintWriter(new FileWriter("OutFile.txt")); 
	for (int i = 0; i < SIZE; i++) { 
		out.println("Value at: " + i + "=" + list.get(i)); 
	} 

} catch (FileNotFoundException e) { 
	System.err.println("FileNotFoundException: "
		+ e.getMessage()); 
	throw new SampleException(e); 

} catch (IOException e) { 
	System.err.println("Caught IOException: "
		+ e.getMessage()); 
}
