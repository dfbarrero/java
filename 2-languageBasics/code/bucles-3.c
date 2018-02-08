void invertir(char cadena[]) {
	int i, j;
	char c;

	for (i=0, j=strlen(cadena)-1; i<j; i++, j--) {
		c = cadena[i];
		cadena[i] = cadena[j];
		cadena[j] = c;
	}
}
