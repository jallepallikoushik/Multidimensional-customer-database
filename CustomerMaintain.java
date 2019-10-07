
import java.util.*;
import java.io.*;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.HashMap;


public class CustomerMaintain {
    static int[] categories;
    static final int NUM_CATEGORIES = 1000, MOD_NUMBER = 997;
    static int DEBUG = 9;
    private int phase = 0;
    private long startTime, endTime, elapsedTime;
    private static int SIZE=100000;
    private int index=0;
    CustomerMaintain()
    {
    	for(int i=0;i<SIZE;i++)
    	{
    	custlist[i]=new Customer();
    	}
    }
    
    //Hash map with Cust ID as key and Customer object as value
    HashMap<Long,Customer> cust=new HashMap<>(); 
    
    //Array of Customer objects with each object associated with a customer
    Customer[] custlist = new Customer[SIZE];   
    
    //Hash map with category list as key with value as a Tree map of amounts(amount*100)
    //associated with that category. The customers associated with a particular 
    //tree map node(amount) are maintained as list against that tree map node.
    HashMap<Integer,TreeMap<Integer,LinkedList<Long>>> catcust=new HashMap<>();
    
    //Tree map of amounts with list of customers associated with a particular amount 
    //are maintained as value against that tree map bode.
    TreeMap<Integer,LinkedList<Long>> amtcust=new TreeMap<>();
    
    //Hash map with tree set of category lists as key and list of customers 
    //as value whose category list exactly matches with the tree set of categories 
    //maintained in the key.
    HashMap<TreeSet<Integer>,LinkedList<Long>> samesame=new HashMap<>();  
    
    //Customer class with customer id, list of categories he/she is interested in,
    //amount spent by the customer on the retailer(viz., Amazon), 
    //number of times he/she made a purchase with the retailer.
    class Customer
    { 
    	long id;
    	TreeSet<Integer> categories;  
    	double dollars;
    	int noOfTimes=0;
    	Customer()
    	{
    		id=0;
    		categories=new TreeSet<>(); 
    		dollars=0;
    	}
    }
    public static void main(String[] args)  throws FileNotFoundException {
	categories = new int[NUM_CATEGORIES];
	Scanner in;
	if(args.length > 0) {
	    in = new Scanner(new File(args[0]));
        } else {
	    in = new Scanner(System.in);
	}
	CustomerMaintain x = new CustomerMaintain();
	x.timer();
	long rv = x.driver(in);
	System.out.println(rv);
	x.timer();
    }

    /** Read categories from in until a 0 appears.
     *  Values are copied into static array categories.  Zero marks end.
     * @param in : Scanner from which inputs are read
     * @return : Number of categories scanned
     */
    public static int readCategories(Scanner in) {
	int cat = in.nextInt();
	int index = 0;
	while(cat != 0) {
	    categories[index++] = cat;
	    cat = in.nextInt();
	}
	categories[index] = 0;
	return index;
    }

    public long driver(Scanner in) {
      String s;
      long rv = 0, id;
      int cat;
      double purchase;

      while(in.hasNext()) {
	  s = in.next();
	  if(s.charAt(0) == '#') {
	      s = in.nextLine();
	      continue;
	  }
	  if(s.equals("Insert")) {
	      id = in.nextLong();
	      readCategories(in);
	      rv += insert(id, categories);
	  } else if(s.equals("Find")) {
	      id = in.nextLong();
	      rv += find(id);
	  } else if(s.equals("Delete")) {
	      id = in.nextLong();
	      rv += delete(id);
	  } else if(s.equals("TopThree")) {
	      cat = in.nextInt();
	      rv += topthree(cat);
	  } else if(s.equals("AddInterests")) {
	      id = in.nextLong();
	      readCategories(in);
	      rv += addinterests(id, categories);
	  } else if(s.equals("RemoveInterests")) {
	      id = in.nextLong();
	      readCategories(in);
	      rv += removeinterests(id, categories);
	  } else if(s.equals("AddRevenue")) {
	      id = in.nextLong();
	      purchase = in.nextDouble();
	      rv += addrevenue(id, purchase);
	  } else if(s.equals("Range")) {
	      double low = in.nextDouble();
	      double high = in.nextDouble();
	      rv += range(low, high);
	  } else if(s.equals("SameSame")) {
	      rv += samesame();
	  } else if(s.equals("NumberPurchases")) {
	      id = in.nextLong();
	      rv += numberpurchases(id);
	  } else if(s.equals("End")) {
	      return rv % 997;
	  } else {
	      System.out.println("Houston, we have a problem.\nUnexpected line in input: "+ s);
	      System.exit(0);
	  }
      }
      rv = rv % MOD_NUMBER;

      return rv;
    }

    public void timer()
    {
        if(phase == 0) {
	    startTime = System.currentTimeMillis();
	    phase = 1;
	} else {
	    endTime = System.currentTimeMillis();
            elapsedTime = endTime-startTime;
            System.out.println("Time: " + elapsedTime + " msec.");
            memory();
            phase = 0;
        }
    }

    public void memory() {
        long memAvailable = Runtime.getRuntime().totalMemory();
        long memUsed = memAvailable - Runtime.getRuntime().freeMemory();
        System.out.println("Memory: " + memUsed/1000000 + " MB / " + memAvailable/1000000 + " MB.");
    }

    /**
     * 
     * @param id: Customer ID
     * @param categories: Array of categories the customer is interested 
     *                    in with zero at the end of the category list.
     * @return: 1 if insertion is successful, otherwise -1.
     */
    int insert(long id, int[] categories) {   	
    	//If customer ID does not exist in the custlist array of objects
    	if(!cust.containsKey(id)){
	    	int i=0;
	    	custlist[index].id=id;
	    	custlist[index].dollars=0;
	    	
	    	//create a Treeset of categories for same same check.
	    	TreeSet<Integer> sameset =new TreeSet<>();
	    	
	    	//For amtcust data structure:
	    	//compute amount*100, then check whether key exists, with this amount. 
	    	int intamount=(int)custlist[index].dollars*100;
	    	
	    	//If present, get the list and add customer to it.
	    	if(amtcust.containsKey(intamount)){ 
	    		LinkedList<Long> l=amtcust.get(intamount);
	    		l.add(id);
	    	}
	    	 //else, create a list, add customer to it, add this list to 'amtcust' treemap.
	    	else{
	    		LinkedList<Long> l=new LinkedList<>();
	    		l.add(id);
		    	amtcust.put(intamount, l);	
	    	}
	    	
	    //For catcust
    	while(categories[i]!=0)
    	{
    		//add category being iterated to the custlist data structure
	    	custlist[index].categories.add(categories[i]);    
	    	
	    	//If the category is not present in the catcust key, 
	    	if(!catcust.containsKey(categories[i]))
	    	{
		    	//create a treemap with customer's amount. add list to it.
	    		TreeMap<Integer,LinkedList<Long>> x=new TreeMap<>();
		    	//create a list, add customer to it.
	    		LinkedList<Long> y=new LinkedList<>();
	    		y.add(id);
	    		int amt=(int) (custlist[index].dollars*100);
                //add amount node with its customer list to the tree map
	    		x.put(amt, y);
		    	//now, add this tree map to category 'catcust' data structure.
	    		catcust.put(categories[i], x);
	    	}
	    	else
	    	{    
	    		TreeMap<Integer,LinkedList<Long>> t=catcust.get(categories[i]); 
	    		int amt=(int) (custlist[index].dollars*100);
	    		//tree map does not exist with customer's amount. 
	    	    if(!t.containsKey(amt))
	    	    {
	    	    	//create a linked list, add customer to it, then add this list to tree map
	    	    	LinkedList<Long> y=new LinkedList<>();
	    	    	y.add(id);
	    	    	t.put(amt, y);
	    	    }
	    	    else// tree map exists
	    	    {
	    	    	//get the list of customers against tree map amount and add it
	    	    	LinkedList<Long> l=t.get(amt);
	    	    	l.add(id);
	    	    }
    	     }
	    	
	    	//for same same data structure:	
	    	//add the category list to the tree set 'sameset'.
	    	sameset.add(categories[i]); 	
    	     i++;
    	}  	
    	    //For same same data structure
    	    //If 'sameset' is present, get the list of customers. 
    	    //add this new customer to it.
    	    if(samesame.containsKey(sameset)){
    	    	LinkedList<Long> clist=samesame.get(sameset);
    	    	clist.add(id);  	
    	    }
    	    else{//'sameset' not present,
    	    	 //create a treeset entry with key as 'sameset', value as customer linked list
    	    	LinkedList<Long> clist=new LinkedList<>();
    	    	clist.add(id);
    	    	samesame.put(sameset, clist);
    	    }
    	   
    	    //add the customer to 'cust' hashmap.
	    	cust.put(id,custlist[index]);
	    	index++;    	
	     return 1;
	    }
    	else//If customer already present, return -1.
    	 return -1;	
        }
    
    /**
     * Find the customer with given id. return 1 if customer found,else -1.
     * @param id: Customer ID
     * @return: 1 if customer found successfully,otherwise -1.
     */
    int find(long id) { 
    	//If customer present with this id,
    	if(cust.containsKey(id)){
    	//get the customer object with id as key. 
    	Customer cobj=cust.get(id);
    	//get the amount associated with the customer
    	return (int)Math.floor(cobj.dollars);
    	}
    	else//if customer not present, return -1.
    	return -1;
        }
    
    /**
     * Delete the customer with given id
     * @param id: customer id
     * @return: 1 if customer deleted successfully, else if customer not exists, return -1.
     */
    int delete(long id) { 
    	if(cust.containsKey(id)){
    		Customer cobj=cust.get(id);
    		int amt=(int)(cobj.dollars*100);
    		
    		//update in amtcust data structure
    		if(amtcust.containsKey(amt)){
    			LinkedList<Long> l=amtcust.get(amt);
    			l.remove(id);
    			if(l.size()==0)
     	    		samesame.remove(cobj.categories);
    		}
    		
    		//update in catcust data structure
    		//remove the id from the category structure after getting the 
    		//amount maintained in each category.
    		Iterator<Integer> i=cobj.categories.iterator();
    		Integer it=next(i);
    		 while(it!=null)
    		{
    			TreeMap<Integer,LinkedList<Long>> t=catcust.get(it);
    			LinkedList<Long> l =t.get(amt);
    			l.remove(id);
    			if(l.size()==0)
    			 t.remove(amt);
    			if(t.size()==0)
    			 catcust.remove(it);
    			 it=next(i);
    		}
    		 
    		//For samesame data structure
    		//remove categories maintained in the samesame data structure.
    		 if(samesame.containsKey(cobj.categories)){
    			 LinkedList<Long> clist=samesame.get(cobj.categories);
    			 clist.remove(id);
    			 if(clist.size()==0)
    				 samesame.remove(cobj.categories);
    		 }
    		 
    		cust.remove(id); //remove customer and return customer's amount
    		return amt/100;
    	}
    	else //If customer doesn't exist, return -1
    		return -1;
    	}
    
    /**
     * returns next element if exist, other wise returns null.
     * @param it Iterator
     * @return next element in the iteration
     */
    public static<T extends Comparable<?super T>> T next(Iterator<T> it)
	{
		
		if(it.hasNext())         //If there exists some next element
		return  it.next();       //return this next element
		else                    //If reached end of the list, then return null
		return null;	
	}
    
    /**
     * Given a category, get the sum of amounts of top 3 customers
     * truncated to dollars associated with this category.
     * @param cat: Category 
     * @return: sum of the amounts of top three customers.
     */
  int topthree(int cat) { 
	  //get the tree map associated with the given category from 
	  //category 'catcust' data structure
        TreeMap<Integer,LinkedList<Long>> t=catcust.get(cat);
       //get the decending order of the map
        NavigableMap<Integer,LinkedList<Long>> nmap=t.descendingMap();
        double totalamount=0;
        int topcount=3;
        
        //loop the navigable map arranged in descending order based on amount
  outer:for(Map.Entry<Integer,LinkedList<Long>> m:nmap.entrySet()){
	       //get the top customer id's list
           LinkedList<Long> l=m.getValue();
           Iterator<Long> i=l.iterator();
           Long custid=next(i);
           while(custid!=null && topcount>0)
           {
        	   //get the customer id's object and then get the amount.
        	  Customer c=cust.get(custid);
        	  //add this amount to totalamount
        	  totalamount=totalamount+c.dollars;
        	  custid=next(i);
        	  //topcount will be decremented. whose value becomes 0 after 
        	  //top 3 customer amounts are added.
        	  topcount--;
           }
           //Once, topcount is zero, means the top three customer's sum is computed.
           //break out of looping the descending order navigable map.
           if(topcount==0)
        	 break outer;
        }
    	return (int)Math.floor(totalamount); }
  
  
  /**
   * Add new interests to the list of a customer's categories.  
   * Some of them may already be in the list of categories of this customer.  
   * Return the number of new categories added to that customer's record.
     Return -1 if no such customer exists.
   * @param id: Customer ID
   * @param categories: category array
   * @return: number of new categories added if customer exists, else return -1;
   */
    int addinterests(long id, int[] categories) { 
    	if(cust.containsKey(id))
    	{
    	Customer cobj=cust.get(id);
    	TreeSet<Integer> prevcat=cobj.categories;
    	int i=0,newcat=0;
    	
    	 if(samesame.containsKey(prevcat)){
 	    	LinkedList<Long> clist=samesame.get(prevcat);
 	    	clist.remove(id);
 	    	if(clist.size()==0)
 	    		samesame.remove(prevcat);
 	    }
    	 
    	while(categories[i]!=0){
    		if(!cobj.categories.contains(categories[i])){
    			cobj.categories.add(categories[i]);
    			newcat++;
    			
    			//update in catcust field
    			int amt=(int)(cobj.dollars*100);
    			if(catcust.containsKey(categories[i]))
    			{
    				TreeMap<Integer,LinkedList<Long>> t= catcust.get(categories[i]);
	    			 if(!t.containsKey(amt))
	 	    	    {
	 	    	    	LinkedList<Long> l1=new LinkedList<>();
	 	    	    	l1.add(id);
	 	    	    	t.put(amt, l1);
	 	    	    }
	 	    	    else
	 	    	    {
	 	    	    	LinkedList<Long> l2=t.get(amt);
	 	    	    	l2.add(id);
	 	    	    }
    			}
    			else
    			{
    				TreeMap<Integer,LinkedList<Long>> t=new TreeMap<>();
    				LinkedList<Long> l=new LinkedList<>();
    				l.add(id);
    				t.put(amt, l);
    				catcust.put(categories[i],t);
    			}
        	}    		
    		i++;
    	    }
    	
    	    //For samesame data structure
    	 if(samesame.containsKey(cobj.categories)){
  	    	LinkedList<Long> getlist=samesame.get(cobj.categories);
  	    	getlist.add(id);
  	    }
  	    else{
  	    	LinkedList<Long> newlist=new LinkedList<>();
  	    	newlist.add(id);
  	    	samesame.put(cobj.categories,newlist);
  	    }
    	   
    	return newcat;
    	  }
    	else
        return -1;
    	}
    
    /**
     * Remove some categories from the list of categories associated 
     * with a customer. Return the number of categories left in the 
     * customer's record. It is possible that the user has no categories 
     * of interest after this step (if all his categories of interest are removed).
     * Return -1 if no such customer exists.
     * @param id: customer ID
     * @param categories: categories array
     * @return: number of remaining categories of interest for the given customer
     *         -1 if customer does not exist.
     */
    int removeinterests(long id, int[] categories) { 
    	if(cust.containsKey(id)){
    	    Customer cobj=cust.get(id);
    	    TreeSet<Integer> prevcat=cobj.categories;
    	    int i=0;
    	    
    	    if(samesame.containsKey(prevcat)){
     	    	LinkedList<Long> clist=samesame.get(prevcat);
     	    	clist.remove(id);
     	    	if(clist.size()==0)
     	    		samesame.remove(prevcat);
     	    }
    	    
    	    while(categories[i]!=0){
    	    	if(cobj.categories.contains(categories[i])){
    	    	 	cobj.categories.remove(categories[i]);
    	    	}
    	    	
    	    	//update in catcust data structure
    	    	if(catcust.containsKey(categories[i]))
    	    	{
    	    		TreeMap<Integer,LinkedList<Long>> t=catcust.get(categories[i]);
    	    		LinkedList<Long> l=t.get((int)(cobj.dollars*100));
    	    		l.remove(id);
    	    		if(l.size()==0)
    	    		 t.remove((int)(cobj.dollars*100));
    	    		if(t.size()==0)
    	    		 catcust.remove(categories[i]);
    	    	}
	    	 	i++;
    	    } 	
    	    	    
    	  //For samesame data structure
        	 if(samesame.containsKey(cobj.categories)){
      	    	LinkedList<Long> getlist=samesame.get(cobj.categories);
      	    	getlist.add(id);
      	    }
      	    else{
      	    	LinkedList<Long> newlist=new LinkedList<>();
      	    	newlist.add(id);
      	    	samesame.put(cobj.categories,newlist);
      	    }
        	 
    	    return cobj.categories.size();
    	}
    	else
    		return -1;	
    	}
    /**
     * update customer record by adding a purchase amount spent by a 
     * customer on our company's product. Returns the net amount spent by the 
     * customer after adding this purchase, truncated to just dollars 
     * (-1 if no such customer exists).
     * @param id: Customer ID
     * @param purchase: amount to be added to the current customer's amount
     * @return: current amount after adding the purchase amount
     *          -1 if the customer does not exist
     */
    int addrevenue(long id, double purchase) { 
    	Customer cobj=cust.get(id);
    	//get the previous amount before purchase is made
    	int prevamt=(int)(cobj.dollars*100);
    	cobj.dollars=cobj.dollars+purchase;
    	//get the current amount after purchase is done
		int curramt=(int)(cobj.dollars*100);
		
		//increment noOftimes when ever customer makes a purchase
		cobj.noOfTimes++;
		
    	//For amtcust data structure
		//If the previous amount present in amount 'amtcust' data structure 
    	if(amtcust.containsKey(prevamt)){
    		LinkedList<Long> l1=amtcust.get(prevamt);
    		//remove the customer id from the list maintained against this previous amount.
    		l1.remove(id);
    		//if the list becomes empty, delete the entry associated with previous amount
    		//from 'amtcust' data structure
    		if(l1.size()==0){
    		  amtcust.remove(prevamt);}
    		//get the current amount entry from 'amtcust' if exists.
    		 if(amtcust.containsKey(curramt)){
    			LinkedList<Long> l2=amtcust.get(curramt);
    			//add customer id to that already existing list
    	    	l2.add(id); 
    		 }
    		 else{ //if the current amount entry not present, create such entry.
    			LinkedList<Long> l3=new LinkedList<>();
    			l3.add(id);
    			amtcust.put(curramt,l3);
    		 }
    	}
    	
    	//update in catcust data structure
   		Iterator<Integer> i=cobj.categories.iterator();
		Integer it=next(i);
        while(it!=null)
    	{
    		TreeMap<Integer,LinkedList<Long>> t=catcust.get(it);
    		if(t.containsKey(prevamt))
    		{
    			LinkedList<Long> l1=t.get(prevamt);
    			l1.remove(id);
    			if(t.containsKey(curramt))
    			{
    				LinkedList<Long> l2=t.get(curramt);
    				l2.add(id);
    			}
    			else
    			{
    				LinkedList<Long> l3=new LinkedList<>();
    				l3.add(id);
    				t.put(curramt, l3);
    			}
    		}
    		it=next(i);
    	}
    	return curramt/100; 
    	}
    
    /**
     * 
     * @param low : minimum amount purchased
     * @param high : maximum amount purchased
     * @return: sum of customer with their amounts in the given value range 'low' and 'high'
     */

    int range(double low, double high) { 
    	
    	//get the smallest value greater than or equal to 'low' present in the tree map.
    	int intlow=amtcust.ceilingKey((int)(low*100));
    	//get the largest value lesser than or equal to 'high' present in the tree map. 
    	int inthigh=amtcust.floorKey((int)(high*100));
    	int rangecount=0;
    	//get the submap within intlow and inthigh inclusive.
    	NavigableMap<Integer,LinkedList<Long>> nmap= 
    			                 amtcust.subMap(intlow, true, inthigh, true);
    	
    	//Iterate through the submap
    	for(Map.Entry<Integer,LinkedList<Long>> m:nmap.entrySet()){
    		//get the customer list associated with the amount being iterated
    		LinkedList<Long> l=m.getValue();
    		//compute number of customers in the list and add this to rangecount.
    		rangecount=rangecount+l.size();
    	}	
    	return rangecount; //contains the number of customers with the amount between low and high.
    	}
    
    /**
     * 
     * @return: The number of customers who satisfy ALL of the following conditions:
               1. They each have 5 or more categories of interest.
               2. Each of these customers has at least one other customer 
                  who has exactly the same set of interests as him/her.
     */
    int samesame() { 
    	int samecount=0;
    	//loop through the table samesame.
    	for(Map.Entry<TreeSet<Integer>,LinkedList<Long>> m:samesame.entrySet()){
    		//If the number of categories are greater thn or equal to 5
    		if(m.getKey().size()>=5){
    			//check if there are more than one customer with such exact list
    			if(m.getValue().size()>1)
    		    //if more than one customer is with exact same categories, 
    		    //then count such customers
    		  	samecount+=m.getValue().size();
    		}
    		else //if such category list not present in 'samesame' dat structure,then go to next entry
    		  continue;		
    	}	
    	return samecount; } //contains number of customers satisfying the two essential criteria
    
    /**
     * 
     * @param id: customer ID
     * @return: number of purchases made by the customer with given id. 
     *          returns -1 if customer does not exist
     */
    int numberpurchases(long id) { 
    	//if customer exists
    	if(cust.containsKey(id)){
    		//get the no of times count maintained in customer object
    		//which determines number of times customer had made a purchase.
    		Customer cobj=cust.get(id);
    		return cobj.noOfTimes; }
    	else
    	 return -1;//return -1 if customer does not exist
       }
}