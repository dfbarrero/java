int[] array = {32, 87, 3, 589, 12, 1076, 2000, 8}; 
int searchfor = 12; 
int i; 
boolean foundIt = false; 

for (i = 0; i < array.length; i++) { 
	if (array[i] == searchfor) {
		foundIt = true; 
		break; 
	} 
}
if (foundIt) 
	System.out.println("Found " + searchfor + " at " + i);
else 
	System.out.println(searchfor + " not in the array");
