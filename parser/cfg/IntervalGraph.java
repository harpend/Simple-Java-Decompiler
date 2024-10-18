package parser.cfg;

import java.util.ArrayList;
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

    public IntervalGraph(Map<Integer, Set<Integer>> successors, Map<Integer, Set<Integer>> predecessors, ControlFlowGraph cfg) {
        this.formerSuccs = successors;
        this.formerPreds = predecessors;
        this.visited = new HashSet<>();
    }

    public List<IntervalNode> findIntervals(int n0) {
        List<IntervalNode> intervals = new ArrayList<>();
        Queue<Integer> workList = new LinkedList<>();
        workList.add(n0);

        while (!workList.isEmpty()) {
            int h = workList.poll(); 

            IntervalNode interval = createInterval(h);

            intervals.add(interval);  
            visited.addAll(interval.bbIds);

            // a node is added to the worklist if its not already "visited", and at least one of its immediate predecessors are in the previously calculated interval
            for (Integer node : formerPreds.keySet()) {
                if (visited.contains(node)) {
                    continue;
                }

                if (!interval.bbIds.contains(node)) {
                    for (int predNode : formerPreds.get(node)) {
                        if (interval.bbIds.contains(predNode)) {
                            workList.add(node);
                            break;
                        }
                    }
                }
            }
        }

        return intervals;
    }

    private IntervalNode createInterval(int h) {
        Set<Integer> intervalSet = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(h);
        intervalSet.add(h);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            // a node is part of an interval if all predecessors are already in the interval, or it has no predecessors
            for (int succ : formerSuccs.get(current)) {
                if (!intervalSet.contains(succ)) {
                    Set<Integer> predsOfSucc = formerPreds.get(succ);
                    if (predsOfSucc == null || intervalSet.containsAll(predsOfSucc)) {
                        intervalSet.add(succ);
                        queue.add(succ); 
                    }
                }
            }
        }

        IntervalNode interval = new IntervalNode(intervalSet);
        return interval;
    }

    
}
