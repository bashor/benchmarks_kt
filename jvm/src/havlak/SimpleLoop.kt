package havlak

import kotlin.concurrent.thread

public open class SimpleLoop() {
    public open fun addNode(bb : BasicBlock) : Unit {
        basicBlocks.add(bb)
    }
    public open fun addChildLoop(loop : SimpleLoop) : Unit {
        children.add(loop)
    }
    public open fun dump(indent : Int) : Unit {
        for (i in 0..indent - 1) System.out.format("  ")
        System.out.format("loop-%d nest: %d depth %d %s", counter, nestingLevel, depthLevel, (if (isReducible)
            ""
        else
            "(Irreducible) "))
        if (children.size != 0)
        {
            System.out.format("Children: ")
            for (loop in children)
            {
                System.out.format("loop-%d ", loop.counter)
            }
        }

        if ((basicBlocks.size()) != 0)
        {
            System.out.format("(")
            for (bb : BasicBlock? in basicBlocks)
            {
                System.out.format("BB#%d%s", bb?.getName(), (if (header == bb)
                    "* "
                else
                    " "))
            }
            System.out.format("\b)")
        }

        System.out.format("\n")
    }

    private var basicBlocks = ObjectSet<BasicBlock>()
    var children  = ObjectSet<SimpleLoop>()
    var parent : SimpleLoop? = null
        set(parent : SimpleLoop?) {
            $parent = parent
            $parent?.addChildLoop(this)
        }
    var header : BasicBlock? = null
        set(bb : BasicBlock?) {
            basicBlocks.add(bb!!)
            $header = bb
        }
    var isRoot : Boolean = false
    var isReducible : Boolean = false
    var counter : Int = 0
    var nestingLevel : Int = 0
        set(level: Int) {
            $nestingLevel = level
            if (level == 0)
                isRoot = true
        }

    var depthLevel : Int = 0
}

public class ObjectSet<T>() : Iterable<T> {
    var arr = arrayOfNulls<Any>(2)
    var size : Int = 0
    fun add(e : T) : Boolean {
        for (i in 0..size - 1) {
            if (arr[i] == e)
            {
                return false
            }

        }
        if (size == (arr.size))
        {
            resize++
            var old = arr
            arr = arrayOfNulls<Any>(arr.size shl 1)
            System.arraycopy(old, 0, arr, 0, old.size)
        }

        arr[size] = e
        size++
        return true
    }
    fun size() : Int {
        return size
    }
    public override fun iterator() : Iterator<T> {
        return object : Iterator<T> {
            var curr : Int = 0
            public override fun hasNext() : Boolean {
                return curr != size
            }
            public override fun next() : T {
                return (arr[curr++] as T)
            }
            public open fun remove() : Unit {
                throw UnsupportedOperationException()
            }


        }
    }
    {
        total++
    }
    class object {
        var total : Int = 0
        var resize : Int = 0

        {
            Runtime.getRuntime().addShutdownHook(thread {
                System.out.println(total.toString() + " = total")
                System.out.println(resize.toString() + " = resize")
            })
        }
    }
}

