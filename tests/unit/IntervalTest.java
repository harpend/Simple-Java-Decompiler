package unit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import parser.cfg.IntervalGraph;
import parser.cfg.IntervalNode;

public class IntervalTest {
        Set<Integer> set1s = new HashSet<>();
        Set<Integer> set1p = new HashSet<>();
        Set<Integer> set2s = new HashSet<>();
        Set<Integer> set2p = new HashSet<>();
        Set<Integer> set3s = new HashSet<>();
        Set<Integer> set3p = new HashSet<>();
        Set<Integer> set4s = new HashSet<>();
        Set<Integer> set4p = new HashSet<>();
        Set<Integer> set5s = new HashSet<>();
        Set<Integer> set5p = new HashSet<>();
        Set<Integer> set6s = new HashSet<>();
        Set<Integer> set6p = new HashSet<>();
        Set<Integer> set7s = new HashSet<>();
        Set<Integer> set7p = new HashSet<>();
        Set<Integer> set8s = new HashSet<>();
        Set<Integer> set8p = new HashSet<>();
        Set<Integer> set9s = new HashSet<>();
        Set<Integer> set9p = new HashSet<>();
        Set<Integer> set10s = new HashSet<>();
        Set<Integer> set10p = new HashSet<>();
        Set<Integer> set11s = new HashSet<>();
        Set<Integer> set11p = new HashSet<>();
        Set<Integer> set12s = new HashSet<>();
        Set<Integer> set12p = new HashSet<>();
        Set<Integer> set13s = new HashSet<>();
        Set<Integer> set13p = new HashSet<>();
        Set<Integer> set14s = new HashSet<>();
        Set<Integer> set14p = new HashSet<>();
        Set<Integer> set15s = new HashSet<>();
        Set<Integer> set15p = new HashSet<>();
        set1s.add(2);
        set2p.add(1);
        set2s.add(3);
        set2s.add(4);
        set3p.add(2);
        set4p.add(2);
        set3s.add(5);
        set4s.add(5);
        set5p.add(3);
        set5p.add(4);
        set5s.add(6);
        set6p.add(5);
        set6p.add(10);
        set6s.add(11);
        set6s.add(7);
        set7p.add(6);
        set7s.add(8);
        set8p.add(7);
        set8p.add(9);
        set8s.add(9);
        set9p.add(8);
        set9s.add(8);
        set9s.add(10);
        set10p.add(9);
        set10s.add(6);
        set11p.add(6);
        set11s.add(12);
        set11s.add(13);
        set12p.add(11);
        set12s.add(13);
        set12s.add(14);
        set13p.add(11);
        set13p.add(12);
        set13s.add(14);
        set14p.add(12);
        set14p.add(13);
        set14s.add(15);
        set15p.add(14);

        Map<Integer, Set<Integer>> maps = new HashMap<>();
        Map<Integer, Set<Integer>> mapp = new HashMap<>();
  
        mapp.put(1, set1p);
        mapp.put(2, set2p);
        mapp.put(3, set3p);
        mapp.put(4, set4p);
        mapp.put(5, set5p);
        mapp.put(6, set6p);
        mapp.put(7, set7p);
        mapp.put(8, set8p);
        mapp.put(9, set9p);
        mapp.put(10, set10p);
        mapp.put(11, set11p);
        mapp.put(12, set12p);
        mapp.put(13, set13p);
        mapp.put(14, set14p);
        mapp.put(15, set15p);
  
        maps.put(1, set1s);
        maps.put(2, set2s);
        maps.put(3, set3s);
        maps.put(4, set4s);
        maps.put(5, set5s);
        maps.put(6, set6s);
        maps.put(7, set7s);
        maps.put(8, set8s);
        maps.put(9, set9s);
        maps.put(10, set10s);
        maps.put(11, set11s);
        maps.put(12, set12s);
        maps.put(13, set13s);
        maps.put(14, set14s);
        maps.put(15, set15s);

        IntervalGraph ig = new IntervalGraph(maps, mapp, null);
        List<IntervalNode> inList = ig.FindIntervals(1);
        for (IntervalNode n : inList) {
            System.out.println(n.ID);
            System.out.println(n.IDs);
            System.out.println("---------------");
        }

        System.exit(0);
}
