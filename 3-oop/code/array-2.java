class ArrayDemo2 {
    public static void main(String[] args) {
		System.out.println(args[0]); 

		System.out.println(args.length);

		String[][] names = {
		    {"Mr. ", "Mrs. ", "Ms. "},
		    {"Smith", "Jones"}
		};
		System.out.println(names[0][0] + names[1][0]);
		System.out.println(names[0][2] + names[1][1]);
	}
}
