package AC;

import java.util.HashMap;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Set;

public class ACAlgorithm {
	//匹配pattern的个数
	public static int K = 4;
	//记录总的状态数
	public static int newstate = 0;
	public static HashMap<GotoKey, Integer> GotoTable 
		= new HashMap<GotoKey, Integer>();
	public static HashMap<Integer, LinkedList<String>> output = new HashMap<Integer, LinkedList<String>>();
	public static HashMap<Integer, Integer> FailureTable = new HashMap<Integer, Integer>();
	
	public static void main(String[] args)
	{
		//构造转向表
		GetGotoTable();
		//构造失败表
		GetFailureTable();
		//输出output表
		System.out.println("i\toutput(i)");
		//输出output表
		Set<Integer> i = output.keySet();
		for(int key : i)
		{
			System.out.println(key + "\t" + output.get(key));
		}
		System.out.println("输入待匹配的字符串:");
		Scanner sc = new Scanner(System.in);
		String text = sc.next();
		//开始匹配
		GetMatchedStr(text);
		sc.close();
	}
	//进行匹配
	private static void GetMatchedStr(String text) {
		int state = 0;
		for(int i = 0; i < text.length(); i++)
		{
			//当转向表不包含对应字符时按失败表前进
			while(i < text.length() && !ContainsKey(GotoTable, new GotoKey(state, text.substring(i, i+1))))
			{
				//如果失败表也不包含，则按照跳过该字符
				if(null == FailureTable.get(state))
				{
					i++;
					continue;
				}
				state = FailureTable.get(state);
			}
			//按照转向表转向下一个位置state
			if(i < text.length() )
				state = GetNext(GotoTable, new GotoKey(state, text.substring(i, i+1)));
			if(output.containsKey(state))
			{
				System.out.print(i + "\t");
				System.out.println(output.get(state));
			}
		}
	}
	//构造转向表
	private static void GetGotoTable()
	{
		//添加所有的pattern
		for(int i = 0; i < K; i++)
		{
			System.out.println("输入 " + i + " of " + K + "个Pattern：");
			@SuppressWarnings("resource")
			Scanner sc = new Scanner(System.in);
			String newpattern = sc.next();
			enter(newpattern);
		}
		//把所有0—>x都加入到转向表中
		Set<GotoKey> S = GotoTable.keySet();
		LinkedList<String> otherStr = new LinkedList<String>();
		for(GotoKey k : S)
		{
			if(!ContainsKey(GotoTable, new GotoKey(0, k.ConvertStr)))
			{
				if(!otherStr.contains(k.ConvertStr))
					otherStr.add(k.ConvertStr);
			}
		}
		while(otherStr.size() > 0)
		{
			GotoTable.put( new GotoKey(0, otherStr.remove(0)), 0);
		}
	}
	//加入新的pattern
	private static void enter(String newpattern) {
		int state = 0;
		int j = 0;
		GotoKey current = new GotoKey(
				state, newpattern.substring(j, j+1));
		//能走下去，就尽量延用以前的老路子，走不下去，就走下面的for()拓展新路子
		while (ContainsKey(GotoTable, current) && j < newpattern.length())
		{
			//System.out.print("进入");
			state = GetNext(GotoTable, current);
			j++;
			current = new GotoKey(
					state, newpattern.substring(j, j+1));
		}
		//拓展新路子
		for (int p = j; p < newpattern.length(); p++)
		{
			newstate = newstate + 1;
			GotoTable.put(new GotoKey(state, newpattern.substring(p, p+1)), newstate);
			state = newstate;
		}
		//此处state为每次构造完一个pat时遇到的那个状态
		if(!output.containsKey(state))
			output.put(state, new LinkedList<String>());
		output.get(state).add(newpattern);
		
	}

	//获取转向表由起始state经过转向字符转到的state
	private static int GetNext(HashMap<GotoKey, Integer> gotoTable,
			GotoKey current) {
		Set<GotoKey> S = gotoTable.keySet();
		for(GotoKey k : S)
		{
			if(k.StartState == current.StartState && k.ConvertStr.equals(current.ConvertStr))
				return gotoTable.get(k);
		}
		return -1;
	}
	//判断转向表是否包含对应转向
	private static boolean ContainsKey(HashMap<GotoKey, Integer> gotoTable,GotoKey current) {
		Set<GotoKey> S = gotoTable.keySet();
		for(GotoKey k : S)
		{
			if(k.StartState == current.StartState && k.ConvertStr.equals(current.ConvertStr))
				return true;
		}
		return false;
	}
	//构造failure表
	private static void GetFailureTable()
	{
		//按照广度优先搜索
		LinkedList<Integer> queue = new LinkedList<Integer>();
		Set<GotoKey> keys = GotoTable.keySet();
		//将所有起始state为0且终止state不为0的转向加入队列
		for(GotoKey key : keys)
		{
			if(0 != key.StartState)
			{
				continue;
			}
			int s = GetNext(GotoTable, key);
			if(s != 0)
			{
				queue.add(s);
				FailureTable.put(s, 0);
			}
		}
		while(!queue.isEmpty())
		{
			int r = queue.remove(0);
			for(GotoKey key : keys)
			{
				if(key.StartState != r)
				{
					continue;
				}
				String a = key.ConvertStr;
				int s = GetNext(GotoTable, key);
				queue.add(s);
				int state = FailureTable.get(r);
				//如果转向表不包含对应转向，则按照失败表转向
				while(!ContainsKey(GotoTable, new GotoKey(state, a)))
				{
					state = FailureTable.get(state);
				}
				FailureTable.put(s, GetNext(GotoTable, new GotoKey(state, a)));
				//合并output(f(s))到output(s)
				LinkedList<String> outputfs = output.get(FailureTable.get(s));
				if(outputfs != null)
					output.get(s).addAll(outputfs);
			}
			
		}
	}
}