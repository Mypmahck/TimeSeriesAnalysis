package cn.InstFS.wkr.NetworkMining.Miner.Common;

public class LineElement {

	int label = 0;
	int start = 0;
	int end = 0;
	
	public LineElement(){
		
	}
	public LineElement(int label,int start,int end){
		
		this.label = label;
		this.start = start;
		this.end = end;
	}
	public int getLabel() {
		return label;
	}
	public void setLabel(int label) {
		this.label = label;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
}