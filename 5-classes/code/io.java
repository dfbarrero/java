public static void main(String args[]) { 
	InputStreamReader isr = new InputStreamReader(System.in); 
	BufferedReader br = new BufferedReader(isr); 
	
	while (true) { 
		double number; 
		
		try { 
			System.out.print("Number: "); 
			String str = br.readLine(); 
			number = Double.valueOf(str).doubleValue(); 
		} catch (NumberFormatException nfe) { 
			System.out.println("Not a number!"); 
			continue; 
		} catch (IOException e) {
			System.out.println("IO error" + e.getMessage()); 
			continue; 
		} 
		
		System.out.println("Number: " + number); 
	} 
}
