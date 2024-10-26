package parser.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class IntervalGraph {
    Map<Integer, Set<Integer>> formerSuccs; // refers to immediate succs and preds
    Map<Integer, Set<Integer>> formerPreds;
    Set<Integer> visited;
    Map<Integer, IntervalNode> int2node;

    public IntervalGraph(Map<Integer, Set<Integer>> successors, Map<Integer, Set<Integer>> predecessors, ControlFlowGraph cfg) {
        this.formerSuccs = successors;
        this.formerPreds = predecessors;
        this.visited = new HashSet<>();
        this.int2node = new HashMap<Integer,IntervalNode>();
    }

    public List<IntervalNode> FindIntervals(int n0) {
        List<IntervalNode> intervals = new ArrayList<>();
        Queue<Integer> workList = new LinkedList<>();
        workList.add(n0);
        int i = 0;
        while (!workList.isEmpty()) {
            int h = workList.poll(); 

            IntervalNode interval = createInterval(h, i);
            intervals.add(interval);  
            visited.addAll(interval.IDs);

            // a node is added to the worklist if its not already "visited", and at least one of its immediate predecessors are in the previously calculated interval
            for (Integer node : formerPreds.keySet()) {
                if (visited.contains(node)) {
                    continue;
                }

                if (!interval.IDs.contains(node)) {
                    for (int predNode : formerPreds.get(node)) {
                        if (interval.IDs.contains(predNode)) {
                            workList.add(node);
                            break;
                        }
                    }
                }
            }

            i++;
        }

        linkIntervalGraph(intervals);
        return intervals;
    }

    private IntervalNode createInterval(int h, int id) {
        IntervalNode interval = new IntervalNode(h, id);
        Queue<Integer> queue = new LinkedList<>();
        queue.add(h);
        interval.IDs.add(h);
        int2node.put(h, interval);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            // a node is part of an interval if all predecessors are already in the interval, or it has no predecessors
            for (int succ : formerSuccs.get(current)) {
                if (!interval.IDs.contains(succ)) {
                    Set<Integer> predsOfSucc = formerPreds.get(succ);
                    if (predsOfSucc == null || interval.IDs.containsAll(predsOfSucc)) {
                        interval.IDs.add(succ);
                        int2node.put(succ, interval);
                        queue.add(succ); 
                    }
                }
            }
        }

        return interval;
    }

    private void linkIntervalGraph(List<IntervalNode> intervalGraph) {
        // there is an edge from one interval to another if and only if there is an edge from some node to the header of another interval
        for (IntervalNode n : intervalGraph) {
            // check predecessors for each header and relink if they are in another interval
            Set<Integer> preds = formerPreds.get(n.head);
            if (preds.isEmpty()) {
                continue;
            }

            for (Integer i : preds) {
                if (n.IDs.contains(i)) {
                    continue;
                }

                IntervalNode pred = int2node.get(i);
                pred.succs.add(n.ID);
                n.preds.add(pred.ID);
            }
        }
    }
}
