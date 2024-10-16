package parser.cfg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Intervals {
    Map<Integer, List<Integer>> succs; // refers to immediate succs and preds
    Map<Integer, List<Integer>> preds;
    Set<Integer> visited;
    ControlFlowGraph cfg;

    public Intervals(Map<Integer, List<Integer>> successors, Map<Integer, List<Integer>> predecessors, ControlFlowGraph cfg) {
        this.succs = successors;
        this.preds = predecessors;
        this.visited = new HashSet<>();
        this.cfg = cfg;
    }

    public List<Set<BasicBlock>> findIntervals(int n0) {
        List<Set<BasicBlock>> intervals = new ArrayList<>();
        Queue<Integer> workList = new LinkedList<>();
        workList.add(n0);

        while (!workList.isEmpty()) {
            int h = workList.poll(); 
            if (visited.contains(h)) {
                continue; 
            }

            Set<Integer> interval = createInterval(h);
            Set<BasicBlock> bbInterval = new HashSet<>();
            for (Integer i : interval) {
                bbInterval.add(cfg.i2bb.get(i));
            }
            
            intervals.add(bbInterval);  
            visited.addAll(interval);

            for (BasicBlock bb : cfg.bbList) {
                int node = bb.id;
                if (!interval.contains(node)) {
                    for (int predNode : preds.get(node)) {
                        if (interval.contains(predNode)) {
                            workList.add(node);
                            break;
                        }
                    }
                }
            }
        }

        return intervals;
    }

    private Set<Integer> createInterval(int h) {
        Set<Integer> interval = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(h);
        interval.add(h);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            for (int succ : succs.get(current)) {
                if (!interval.contains(succ)) {
                    List<Integer> predsOfSucc = preds.get(succ);
                    if (predsOfSucc == null || interval.containsAll(predsOfSucc)) {
                        interval.add(succ);
                        queue.add(succ); 
                    }
                }
            }
        }

        return interval;
    }
}
