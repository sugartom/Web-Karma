package edu.isi.karma.cleaning;

import java.util.Iterator;
import java.util.Vector;

public class ConstrainedAlignment {
	public Vector<Vector<TNode>> olist;
	public Vector<Vector<TNode>> tlist;
	public int expnum;
	public ConstrainedAlignment()
	{
		
	}
	public Vector<Integer> findAll(TNode t,Vector<TNode> x)
	{
		return null;
	}
	// inserted same token  
	public void traverse()
	{
		//inite
		Vector<Integer> spos = new Vector<Integer>();
		for(int i = 0; i<expnum;i++)
		{
			spos.add(0);
		}
		int[] iters = new int[expnum];
		//inite the iters
		int[] curStats = new int[expnum];
		for(int i = 0; i<iters.length; i++)
		{
			iters[i] = 0;
		}
		for(int i = 0; i<expnum; i++)
		{
			
		}
		
		
	}
}
class LatentState{
	static final int SEG = 1;//a segement state
	static final int UND = 2;
	static final int ANC = 3;//a anchor state
	public int StateType;
	public LatentState nextState;
	public Vector<int[]> segments;
	public LatentState(Vector<Integer> spos)
	{
		this.StateType = LatentState.UND;
		segments = new Vector<int[]>();
		if(segments.size() !=spos.size())
		{
			System.out.println("Length different");
			return;
		}
		for(int i = 0; i<spos.size(); i++)
		{
			int p = spos.get(i);
			int[] a = {p,-1};
			segments.add(a);
		}
	}
	public void setStart(int s,int lindex)
	{
		segments.get(lindex)[0] = s;
	}
	public void setEnd(int e,int lindex)
	{
		segments.get(lindex)[1] = e;
	}
	public void createNewState(Vector<Integer> spos)
	{
		LatentState nState =new LatentState(spos);
		this.nextState = nState;
	}
	public void settype(int type)
	{
		
	}
}