import java.util.*;
class Sort implements Runnable{
    //parametros de quicksort
    int[] arreglo;
    int start,end;
    Sort(int[] data,int first,int last) {
        this.arreglo=data;
        this.start=first;
        this.end=last;
    }
    //corro quicksort
    public void run(){
        quickSort(this.arreglo,this.start,this.end);
    }
    //intercambio valores del arreglo
    static void swap(int[] a, int i, int j){
        int temp=a[i];
        a[i]=a[j];
        a[j]=temp;
    }
    //realizo particion del arreglo
    static int particion(int[] data, int start, int end) {
        if(start==end)
            return start;
        int pivot=data[end];
        int s=start-1;
        for(int i=start;i<end;i++)
            if(data[i]>=pivot)
                swap(data,++s,i);
        swap(data,++s,end);
        return s;
    }
    //algortimo quicksort para ordenar un arreglo con modidicación
    static void quickSort(int[] data, int first, int last) {
        if (last<=first)
            return;
        int s=particion(data,first,last);
        //recursividad
        //primera mitad
        quickSort(data,first,s-1);
        //segunda mitad
        quickSort(data,s+1,last);
    }
    //main
    public static void main(String[] args) {
        Scanner lectura = new Scanner(System.in); 
        System.out.println("Ingresa largo del arreglo");

        int largo = lectura.nextInt();
        //creamos array
        int[ ] array = new int[largo];
        System.out.println("Ingresa los valores del arreglo:");
        for (int i =0; i<largo; i++){
            Scanner valores = new Scanner(System.in); 
            int valor = valores.nextInt();
            array[i]=valor;
        }
        //creamos particion del arreglo
        int s=particion(array,0,largo-1);
        //creamos hilos para cada mitad
        Thread t1=new Thread(new Sort(array,0,s-1));
        Thread t2=new Thread(new Sort(array,s+1,array.length-1));
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        }catch(InterruptedException e){
            System.out.println(e);
        }
        System.out.println("El arreglo ordenado es:");
        for (int i=0;i<largo;i++){
            System.out.print(array[i]);
            System.out.print(" ");
        }
        System.out.println();
    }
}