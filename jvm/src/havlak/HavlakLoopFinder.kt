package havlak

//
// class UnionFindNode
//
// The algorithm uses the Union/Find algorithm to collapse
// complete loops into a single node. These nodes and the
// corresponding functionality are implemented with this class
//
import java.util.ArrayList
import java.util.HashMap


class UnionFindNode {
    var dfsNumber = 0
    var parent: UnionFindNode? = null
    var bb: BasicBlock? = null
    var loop: SimpleLoop? = null

    // Initialize this node.
    //
    fun initNode(bb_arg: BasicBlock, dfsNumber_arg: Int) {
        parent = this;
        bb = bb_arg;
        dfsNumber = dfsNumber_arg;
        loop = null
    }

    // Union/Find Algorithm - The find routine.
    //
    // Implemented with Path Compression (inner loops are only
    // visited and collapsed once, however, deep nests would still
    // result in significant traversals).
    //
    fun findSet(): UnionFindNode {
        val nodeList = ArrayList<UnionFindNode>();

        var node = this;
        while (node != node.parent) {
            if (node.parent != node.parent?.parent)
                nodeList.add(node);

            node = node.parent!!;
        }

        // Path Compression, all nodes' parents point to the 1st level parent.
        for (n in nodeList) {
            n.parent = node.parent;
        }

        return node;
    }

    // Union/Find Algorithm - The union routine.
    //
    // Trivial. Assigning parent pointer is enough,
    // we rely on path compression.
    //
    fun union(unionFindNode: UnionFindNode) {
        parent = unionFindNode;
    }
    fun setLoop(l: SimpleLoop): SimpleLoop {
        loop = l
        return l
    }
}

class HavlakLoopFinder(val cfg: CFG, val lsg: LSG) {

    //
    // enum BasicBlockClass
    //
    // Basic Blocks and Loops are being classified as regular, irreducible,
    // and so on. This enum contains a symbolic name for all these
    // classifications. Python doesn't have enums, so we just create values.
    //
    val BB_TOP = 0
    val BB_NONHEADER = 1
    val BB_REDUCIBLE = 2
    val BB_SELF = 3
    val BB_IRREDUCIBLE = 4
    val BB_DEAD = 5
    val BB_LAST = 6

    //
    // Constants
    //
    // Marker for uninitialized nodes.
    val UNVISITED = -1

    // Safeguard against pathologic algorithm behavior.
    val MAXNONBACKPREDS = 32 * 1024

    //
    // IsAncestor
    //
    // As described in the paper, determine whether a node 'w' is a
    // "true" ancestor for node 'v'.
    //
    // Dominance can be tested quickly using a pre-order trick
    // for depth-first spanning trees. This is why DFS is the first
    // thing we run below.
    //
    fun isAncestor(w: Int, v: Int, last: List<Int>): Boolean {
        return (w <= v) && (v <= last[w]);
    }

    //
    // DFS - Depth-First-Search
    //
    // DESCRIPTION:
    // Simple depth first traversal along out edges with node numbering.
    //
    fun DFS(currentNode: BasicBlock,
            nodes: List<UnionFindNode>,
            number: MutableMap<BasicBlock, Int>,
            last: MutableList<Int>,
            current: Int): Int {
        nodes[current].initNode(currentNode, current)
        number[currentNode] = current

        var lastid = current
        for (target in currentNode.outEdges) {
            if (number[target] == UNVISITED)
                lastid = DFS(target, nodes, number, last, lastid + 1);
        }

        last[number[currentNode]!!] = lastid;
        return lastid;
    }

    //
    // findLoops
    //
    // Find loops and build loop forest using Havlak's algorithm, which
    // is derived from Tarjan. Variable names and step numbering has
    // been chosen to be identical to the nomenclature in Havlak's
    // paper (which, in turn, is similar to the one used by Tarjan).
    //
    fun findLoops(): Int {
        if (cfg.startNode == null) {
            return 0;
        }

        val size = cfg.getNumNodes();

        val nonBackPreds = ArrayList<ArrayList<Int>>(size)
        val backPreds = ArrayList<ArrayList<Int>>(size)
        val number = HashMap<BasicBlock, Int>()
        val header = ArrayList<Int>(size)
        val types = ArrayList<Int>(size)
        val last = ArrayList<Int>(size)
        val nodes   = ArrayList<UnionFindNode>(size)

        size times {
            nonBackPreds.add(ArrayList<Int>())
            backPreds.add(ArrayList<Int>())
            header.add(0)
            types.add(0)
            last.add(0)
            nodes.add(UnionFindNode())
        }

        // Step a:
        //   - initialize all nodes as unvisited.
        //   - depth-first traversal and numbering.
        //   - unreached BB's are marked as dead.
        //
        for (bb in cfg.basicBlockMap.values()) {
            number[bb] = UNVISITED;
        }

        DFS(cfg.startNode, nodes, number, last, 0);

        // Step b:
        //   - iterate over all nodes.
        //
        //   A backedge comes from a descendant in the DFS tree, and non-backedges
        //   from non-descendants (following Tarjan).
        //
        //   - check incoming edges 'v' and add them to either
        //     - the list of backedges (backPreds) or
        //     - the list of non-backedges (nonBackPreds)
        //
        for (w in 0..size - 1) {
            header[w] = 0;
            types[w]  = BB_NONHEADER

            val nodeW = nodes[w].bb
            if (nodeW == null) {
                types[w] = BB_DEAD;
            } else {
                if (nodeW.getNumPred() > 0) {
                    for (nodeV in nodeW.inEdges) {
                        val v = number[nodeV]!!
                        if (v != UNVISITED) {
                            if (isAncestor(w, v, last)) {
                                backPreds[w].add(v);
                            } else {
                                nonBackPreds[w].add(v);
                            }
                        }
                    }
                }
            }
        }

        // Start node is root of all other loops.
        header[0] = 0;

        // Step c:
        //
        // The outer loop, unchanged from Tarjan. It does nothing except
        // for those nodes which are the destinations of backedges.
        // For a header node w, we chase backward from the sources of the
        // backedges adding nodes to the set P, representing the body of
        // the loop headed by w.
        //
        // By running through the nodes in reverse of the DFST preorder,
        // we ensure that inner loop headers will be processed before the
        // headers for surrounding loops.
        //
        for (w in size - 1 downTo 0) {
            // this is 'P' in Havlak's paper
            val nodePool = ArrayList<UnionFindNode>();

            val nodeW = nodes[w].bb
            if (nodeW == null) {
                continue;
            }

            // Step d:
            for (v in backPreds[w]) {
                if (v != w) {
                    nodePool.add(nodes[v].findSet());
                } else {
                    types[w] = BB_SELF;
                }
            }

            // Copy nodePool to workList.
            //
            var workList: MutableList<UnionFindNode> = ArrayList<UnionFindNode>(nodePool) //todo ?
            //            for (int n = 0; n < nodePool.length; ++n) {
            //                  workList.add(nodePool[n]);
            //            }

            if (nodePool.notEmpty()) {
                types[w] = BB_REDUCIBLE;
            }
            // work the list...
            //
            while (workList.notEmpty()) {
                val x = workList[0];
                workList = workList.subList(0, workList.size-1);

                // Step e:
                //
                // Step e represents the main difference from Tarjan's method.
                // Chasing upwards from the sources of a node w's backedges. If
                // there is a node y' that is not a descendant of w, w is marked
                // the header of an irreducible loop, there is another entry
                // into this loop that avoids w.
                //

                // The algorithm has degenerated. Break and
                // return in this case.
                //
                val nonBackSize = nonBackPreds[x.dfsNumber].size
                if (nonBackSize > MAXNONBACKPREDS) {
                    return 0;
                }

                for (iter in nonBackPreds[x.dfsNumber]) {
                    val y = nodes[iter]
                    val ydash = y.findSet()

                    if (!isAncestor(w, ydash.dfsNumber, last)) {
                        types[w] = BB_IRREDUCIBLE;
                        nonBackPreds[w].add(ydash.dfsNumber);
                    } else {
                        if (ydash.dfsNumber != w) {
                            if (nodePool.indexOf(ydash) == -1) {
                                workList.add(ydash);
                                nodePool.add(ydash);
                            }
                        }
                    }
                }
            }

            // Collapse/Unionize nodes in a SCC to a single node
            // For every SCC found, create a loop descriptor and link it in.
            //
            if ((nodePool.size > 0) || (types[w] == BB_SELF)) {
                val loop = lsg.createNewLoop();

                loop.header = nodeW
                if (types[w] == BB_IRREDUCIBLE) {
                    loop.isReducible = true;
                } else {
                    loop.isReducible = false;
                }

                // At this point, one can set attributes to the loop, such as:
                //
                // the bottom node:
                //    iter  = backPreds(w).begin();
                //    loop bottom is: nodes(iter).node;
                //
                // the number of backedges:
                //    backPreds(w).size()
                //
                // whether this loop is reducible:
                //    types(w) != BB_IRREDUCIBLE
                //
                nodes[w].loop = loop;

                for (node in nodePool) {
                    // Add nodes to loop descriptor.
                    header[node.dfsNumber] = w;
                    node.union(nodes[w]);

                    // Nested loops are not added, but linked together.
                    if (node.loop != null) {
                        node.loop!!.parent = loop
                    } else {
                        loop.addNode(node.bb);
                    }
                }
                lsg.addLoop(loop);
            } // nodePool.length
        } // Step c

        return lsg.getNumLoops();
    } // findLoops
} // HavlakLoopFinder
