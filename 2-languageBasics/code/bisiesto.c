#include<stdio.h>

int esBisiesto(int anyo) {
	if (anyo % 4 == 0) {
		if (((anyo%100)==0) && ((anyo%400)!=0)) return 0;
		else return 1;
	}
	return 0;
}
int main() {
	int i;
	for (i=2001; i<=2100; i++) {
		if (esBisiesto(i) != 0) printf("%d\n", i);
	}
	return 0;
}
