package edu.isi.karma.cleaning;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class ProgSynthesis {
	Vector<Vector<TNode>> orgVector = new Vector<Vector<TNode>>();
	Vector<Vector<TNode>> tarVector = new Vector<Vector<TNode>>();
	String bestRuleString = "";
	public PartitionClassifierType classifier;

	public void inite(Vector<String[]> examples) {
		for (int i = 0; i < examples.size(); i++) {
			Ruler r = new Ruler();
			//System.out.println("Example: "+examples.get(i)[0]);
			r.setNewInput(examples.get(i)[0]);
			orgVector.add(r.vec);
			Ruler r1 = new Ruler();
			r1.setNewInput(examples.get(i)[1]);
			tarVector.add(r1.vec);
		}
	}
	public Vector<Vector<Integer>> generateCrossIndex(Vector<Integer> poss,
			Vector<Vector<Integer>> p, int index) {
		Vector<Vector<Integer>> qVector = new Vector<Vector<Integer>>();
		if (index >= poss.size()) {
			return p;
		}
		int curleng = poss.get(index);
		if (p.size() == 0) {
			for (int i = 0; i < curleng; i++) {
				Vector<Integer> x = new Vector<Integer>();
				x.add(i);
				qVector.add(x);
			}
		} else {
			for (int j = 0; j < p.size(); j++) {
				for (int i = 0; i < curleng; i++) {
					Vector<Integer> x = (Vector<Integer>) p.get(j).clone();
					x.add(i);
					qVector.add(x);
				}
			}
		}
		qVector = generateCrossIndex(poss, qVector, index + 1);
		return qVector;
	}


	public double getCompScore(int i, int j, Vector<Partition> pars) {
		Partition p = pars.get(i).mergewith(pars.get(j));
		if(p==null)
		{
			return -Double.MAX_VALUE;
		}
		int validCnt = 0;
		for (int x = 0; x < pars.size(); x++) {
			if (x == i || x == j) {
				continue;
			}
			Partition q = p.mergewith(pars.get(x));
			if (q != null) {
				validCnt++;
			}
		}
		return validCnt;
	}

	public Vector<Partition> initePartitions() {
		Vector<Partition> pars = new Vector<Partition>();
		// inite partition for each example
		for (int i = 0; i < orgVector.size(); i++) {
			Vector<Vector<TNode>> ovt = new Vector<Vector<TNode>>();
			Vector<Vector<TNode>> tvt = new Vector<Vector<TNode>>();
			ovt.add(this.orgVector.get(i));
			tvt.add(this.tarVector.get(i));
			Partition pt = new Partition(ovt, tvt);
			pars.add(pt);
		}
		return pars;
	}

	public void mergePartitions(Vector<Partition> pars) {
		double maxScore = 0;
		int[] pos = { -1, -1 };
		for (int i = 0; i < pars.size(); i++) {
			for (int j = i + 1; j < pars.size(); j++) {
				double s = getCompScore(i, j, pars);
				if(s <0)
				{
					continue;
				}
				if (s >= maxScore) {
					pos[0] = i;
					pos[1] = j;
					maxScore = s;
				}
			}
		}
		if (pos[0] != -1 && pos[1] != -1)
			UpdatePartitions(pos[0], pos[1], pars);
	}

	public void UpdatePartitions(int i, int j, Vector<Partition> pars) {
		Partition p = pars.get(i).mergewith(pars.get(j));
		pars.set(i, p);
		pars.remove(j);
	}

	public String getBestRule()
	{
		return this.bestRuleString;
	}
	public Vector<Partition> ProducePartitions(boolean condense)
	{
		Vector<Partition> pars = this.initePartitions();
		int size = pars.size();
		while(condense)
		{
			this.mergePartitions(pars);
			if(size == pars.size())
			{
				break;
			}
			else
			{
				size = pars.size();
			}
		}
		return pars;
	}
	public Collection<ProgramRule> producePrograms(Vector<Partition> pars) {
		Program prog = new Program(pars);
		ProgramRule r = prog.toProgram1();
		String xString = "";
		int termCnt = 0;
		boolean findRule = true;	
		while((xString=this.validRule(r))!="GOOD" && findRule)
		{
			if(termCnt == 10000)
			{
				findRule = false;
				break;
			}
			for(Partition p:prog.partitions)
			{
				if(p.label.compareTo(xString)==0)
				{
					String newRule = p.toProgram();
					System.out.println("updated Rule: "+p.label+": "+newRule);
					if(newRule.contains("null"))
					{
						findRule = false;
						break;
					}
					r.updateClassworker(xString, newRule);
				}
			}
			termCnt ++;
		}
		HashSet<ProgramRule> rules = new HashSet<ProgramRule>();
		if(findRule)
			rules.add(r);
		System.out.println("*********************************");
		System.out.println("Total program size: "+prog.size());
		System.out.println("Updated times: "+ termCnt);
		System.out.println("*********************************");
		return rules;	
	}
	public Collection<ProgramRule> run_main() {
		Vector<Partition> vp = this.ProducePartitions(true);
		Collection<ProgramRule> cpr = this.producePrograms(vp);
		if(cpr.size() == 0)
		{
			vp = this.ProducePartitions(false);
			cpr = this.producePrograms(vp);
		}
		return cpr;
		
	}
	public String validRule(ProgramRule p)
	{	
		for(int i=0; i<orgVector.size();i++)
		{		
			String s1 = UtilTools.print(orgVector.get(i));
			String labelString = p.getClassForValue(s1);
			//System.out.println("Rule: "+ p.getStringRule(labelString));
			InterpreterType worker = p.getWorkerForClass(labelString);
			String s2 = worker.execute(s1);
			String s3 = UtilTools.print(tarVector.get(i));
			
			//System.out.println("Validation: "+s2+" | "+s3);
			if(s3.compareTo(s2)!=0)
			{
				return labelString;
			}
		}
		return "GOOD";
	}
}