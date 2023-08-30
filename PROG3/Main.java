import java.io.*;
import java.util.*;
import java.math.*;
 
public class Main {
	
	public static int chooseFromDist(double[] arr) {
		double[]u = new double[arr.length];
		u[0] = arr[0];
		for(int i=1;i<arr.length;i++)
			u[i]=u[i-1]+arr[i];
		
		Random r = new Random();
		double k = r.nextDouble();
		for(int i =0;i<arr.length-1;i++) {
			if(k<u[i]) 
				return i;
			}		
		return arr.length-1;	
	}
	public static void extractAnswer(int[][][]LoseCount,int [][][]WinCount,int LTarget,int NDice) {
		int [][]ans=new int[LTarget][LTarget];
		double [][]ansProb=new double[LTarget][LTarget];
		for(int i=0;i<LTarget;i++) {
			for(int j=0;j<LTarget;j++) {
				int numDice =-1;
				double maxProb=-1.0;
				for(int k=1;k<NDice+1;k++) {
					double temp1=WinCount[i][j][k];
					double temp2 = WinCount[i][j][k]+LoseCount[i][j][k];
					double tempProb=0.0;
					if(temp2!=0.0) {
						tempProb = temp1/temp2;
					}
					
					if(maxProb<tempProb) {
						maxProb=tempProb;
						numDice=k;
					}				
				}
				maxProb = Math.round(maxProb*10000.0)/10000.0;
				
				ansProb[i][j]=maxProb;
				if(maxProb==0.0)
					ans[i][j]=0;
				else
				ans[i][j]=numDice;
			}
		}
		for(int i=0;i<LTarget;i++) {
			for(int j=0;j<LTarget;j++) {
				System.out.printf("%2d",ans[i][j]);
			}
			System.out.println("");
		}
		System.out.println("");
		for(int i=0;i<LTarget;i++) {
			for(int j=0;j<LTarget;j++) {
				System.out.printf("%8s",ansProb[i][j]);
			}
			System.out.println("");
		}
	}
	public static int chooseDice(int []score, int[][][]LoseCount,int [][][]WinCount,int NDice,double M) {
		int K=NDice;
		int x = score[0];
		int y =score[1];
		double [] fJ = new double[NDice+1];
		
		double maxfJ=-1.0;
		int maxB=-1;
		
		double g=0.0;
		int T=0;
		
		double []ans = new double[NDice+1];
		for(int i=1;i<fJ.length;i++) {
			double temp =WinCount[x][y][i]+LoseCount[x][y][i];
			double temp2 = WinCount[x][y][i];
			if(temp!=0.0)
			fJ[i]=temp2/temp;
			else 
				fJ[i]=0.5;				
		}
		for(int i=1;i<fJ.length;i++) {
			if(maxfJ<fJ[i]) {
				maxfJ=fJ[i];maxB=i;
				}
		}
		
		for(int i=1;i<fJ.length;i++) {
			if(i!=maxB) 
				g+=fJ[i];
			
			T+=WinCount[x][y][i];
			T+=LoseCount[x][y][i];
		}
		double Ttemp =T;
		double Ktemp = K;
		ans[maxB]=(Ttemp*fJ[maxB]+M)/(Ttemp*fJ[maxB]+Ktemp*M);
		for(int i=1;i<ans.length;i++) {			
			if(i!=maxB) 
				ans[i]=(1.0-ans[maxB])*(Ttemp*fJ[i]+M)/(Ttemp*g+(Ktemp-1.0)*M);				
		}
		
		return chooseFromDist(ans);
	}
	
	public static int[] rollDice(int NDice, int NSides) {
		int Ns = NSides;
		int Nd= NDice;
		int [] ans = new int [Nd];
		for (int i=0;i<Nd;i++) {
			Random r = new Random();
			int ran = r.nextInt(Ns);
			ans[i]=ran+1;
		}
		
		return ans;
	}
	public static ArrayList<int[][][]> PlayGame(int NDice,int NSides,int LTarget, 
			int UTarget,int[][][]LoseCount,int[][][]WinCount,double M){
		//declare List to store matrix
		ArrayList<int[][][]> updateCount=new ArrayList<>();
		
		//initialize parameters
		int numOfturn = 1;
		int aScore=0,bScore=0;
		int winner=0;
		
		//track game 
		ArrayList<ArrayList<Integer>> traceA = new ArrayList<>();
		ArrayList<ArrayList<Integer>> traceB = new ArrayList<>();
		int[] score = new int[2];
		while(aScore<LTarget&&bScore<LTarget) {
			
			
			score[0]=aScore;score[1]=bScore;
			int numofDice = chooseDice(score,LoseCount,WinCount,NDice,M);
		//	int[] diceResult = new int[numofDice];
			int [] diceResult = rollDice(numofDice,NSides);
			if(numOfturn==1) {
				//record current state using the corresponding List
				ArrayList<Integer>temp = new ArrayList<>();
				temp.add(aScore);temp.add(bScore);temp.add(numofDice);
				traceA.add(temp);
				
				//update outcome
				for(int i=0;i<diceResult.length;i++)
					aScore+=diceResult[i];
				
				//check if anyone wins or loses
				if(aScore>UTarget)
					winner=-1;
				else if(aScore>=LTarget&&aScore<=UTarget)
					winner=1;
				}
			else {
				//record current state using the corresponding List
				ArrayList<Integer>temp = new ArrayList<>();
				temp.add(aScore);temp.add(bScore);temp.add(numofDice);
				traceB.add(temp);
				
				//update outcome
				for(int i=0;i<diceResult.length;i++)
					bScore+=diceResult[i];
				
				//check if anyone wins or loses
				if(bScore>UTarget)
					winner=1;
				else if(bScore>=LTarget&&bScore<=UTarget)
					winner=-1;
			}
			//switch
			numOfturn*=-1;			
		}
		//update WinCount & LoseCount
		if(winner==1) {
			for(int i=0;i<traceA.size();i++)
				WinCount[traceA.get(i).get(0)][traceA.get(i).get(1)][traceA.get(i).get(2)]+=1;
			
			for(int i=0;i<traceB.size();i++)
				LoseCount[traceB.get(i).get(0)][traceB.get(i).get(1)][traceB.get(i).get(2)]+=1;
		}
		else {
			for(int i=0;i<traceA.size();i++)
				LoseCount[traceA.get(i).get(0)][traceA.get(i).get(1)][traceA.get(i).get(2)]+=1;
			
			for(int i=0;i<traceB.size();i++)
				WinCount[traceB.get(i).get(0)][traceB.get(i).get(1)][traceB.get(i).get(2)]+=1;
		}	

		updateCount.add(WinCount);updateCount.add(LoseCount);
		return updateCount;
	}
	public static void Prog3(int NDice,int NSides,int LTarget, int UTarget,int NGames, double M) {
		int [][][]WinCount = new int[LTarget][LTarget][NDice+1];
		int [][][]LoseCount = new int[LTarget][LTarget][NDice+1];
		for(int i=1;i<NGames;i++) {
			ArrayList<int[][][]> rec=PlayGame(NDice,NSides,LTarget, 
					UTarget,LoseCount,WinCount,M);
			WinCount = rec.get(0);
			LoseCount=rec.get(1);
		}
		extractAnswer(LoseCount,WinCount,LTarget,NDice);
		
	}
	
    public static void main(String[] args) {
    	Scanner sc=new Scanner(System.in);
    	int numDice=sc.nextInt();
    	int numSides=sc.nextInt();
    	int LowTarget=sc.nextInt();
    	int UpTarget=sc.nextInt();
    	int numGame = sc.nextInt();
    	double M = sc.nextDouble();
    	Prog3(numDice,numSides,LowTarget,UpTarget,numGame,M);

    	
    	sc.close();
    }
}