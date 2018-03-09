public class FindDups { 
	public static void main(String[] args) { 
		Set<String> s = new HashSet<String>(); 
		
		for (String a : args) 
			if (!s.add(a)) 
				System.out.println("Duplicate detected"); 
		
		System.out.println(s.size() + " distinct words: " 
			+ s); 
	} 
}
