package com.sun.labs.kanban.richards_gibbons

class Packet(var link : Packet?, val id : Int, val kind : Int) {
    var a1 : Int = 0
    var a2 : IntArray = IntArray(BUFSIZE + 1)

    class object {
        val BUFSIZE : Int = 3
    }

    fun appendTo(list : Packet?): Packet {
        link = null
        if (list == null)
            return this

        var p: Packet = list
        while (p.link != null) {
            p = p.link!!
        }
        p.link = this

        return list
    }
}

abstract class Task(val id: Int, val pri: Int, val wkq: Packet?, var state: Int) {
    abstract fun fn(pkt : Packet?) : Task?

//    protected fun waitTask() : Task? {
//        state = state or WAITBIT
//        return this
//    }

//    protected fun hold() : Task? {
//        ++holdCount
//        state = state or HOLDBIT
//        return link
//    }
//    protected fun release(i : Int) : Task? {
//        var t : Task? = findtcb(i)
//        t?.state = t?.state and (HOLDBIT).inv()
//        if ((t?.pri)!! > pri)
//            return t
//        return this
//    }
//    protected fun qpkt(pkt : Packet?) : Task? {
//        var t : Task? = findtcb((pkt?.id)!!)
//        if (t == null)
//            return t
//        qpktCount++
//        pkt?.link = null
//        pkt?.id = id
//        if (t?.wkq == null)
//        {
//            t?.wkq = pkt
//            t?.state = t?.state or PKTBIT
//            if ((t?.pri)!! > pri)
//                return t
//        }
//        else
//            t?.wkq = pkt?.append_to(t?.wkq)
//        return this
//    }
    var link : Task? = null
    {
//        link = taskList
//        taskList = this
//        taskTab[i] = this
    }
    class object {
//        fun findtcb(id : Int) : Task? {
//            var t : Task? = null
//            if (1 <= id && id <= Task.TaskTabSize)
//                t = Task.taskTab[id]
//            if (t == null)
//                System.out?.println("\nBad task id " + id)
//            return t
//        }
//        fun schedule() : Unit {
//            var t : Task? = taskList
//            while (t != null)
//            {
//                var pkt : Packet? = null
//                when (t?.state) {
//                    S_WAITPKT -> {
//                        pkt = t?.wkq
//                        t?.wkq = pkt?.link
//                        t?.state = ((if (t?.wkq == null)
//                            S_RUN
//                        else
//                            S_RUNPKT))
//                        if (tracing)
//                            trace((('0' + (t?.id)!!) as Char))
//                        t = t?.fn(pkt)
//                    }
//                    S_RUN, S_RUNPKT -> {
//                        if (tracing)
//                            trace((('0' + (t?.id)!!) as Char))
//                        t = t?.fn(pkt)
//                    }
//                    S_WAIT, S_HOLD, S_HOLDPKT, S_HOLDWAIT, S_HOLDWAITPKT -> {
//                        t = t?.link
//                    }
//                    else -> {
//                        return
//                    }
//                }
//            }
//        }
//        fun trace(a : Char) : Unit {
//            if (--layout <= 0)
//            {
//                System.out?.println()
//                layout = 50
//            }
//            System.out?.print(a)
//        }
//        var layout : Int = 0
        val PKTBIT : Int = 1
        val WAITBIT : Int = 2
        val HOLDBIT : Int = 4
        val S_RUN : Int = 0
        val S_RUNPKT : Int = PKTBIT
        val S_WAIT : Int = WAITBIT
        val S_WAITPKT : Int = WAITBIT + PKTBIT
        val S_HOLD : Int = HOLDBIT
        val S_HOLDPKT : Int = HOLDBIT + PKTBIT
        val S_HOLDWAIT : Int = HOLDBIT + WAITBIT
        val S_HOLDWAITPKT : Int = HOLDBIT + WAITBIT + PKTBIT
//        val TaskTabSize : Int = 10
//        var taskTab : Array<Task?>? = Array<Task?>(TaskTabSize)
//        var taskList : Task? = null
//        val tracing : Boolean = false
//        var holdCount : Int = 0
//        var qpktCount : Int = 0
    }
}

//fun DeviceTask(id : Int, pri : Int, wkq : Packet?) = DeviceTask(id, pri, wkq, if (wkq != null) Task.S_WAITPKT else Task.S_WAIT)

//class DeviceTask(id : Int, pri : Int, wkq : Packet?, state: Int) : Task(id, pri, wkq, state) {
//    override fun fn(pkt : Packet?) : Task? {
//        if (pkt == null)
//        {
//            if (v1 == null)
//                return waitTask()
//            pkt = v1
//            v1 = null
//            return qpkt(pkt)
//        }
//        else
//        {
//            v1 = pkt
//            if (tracing)
//                trace((pkt?.a1 as Char))
//            return hold()
//        }
//    }
//    private var v1 : Packet? = null
//    {
//        v1 = null
//    }
//}

//class HandlerTask(i : Int, p : Int, w : Packet?) : Task(i, p, w, ((if (w != null)
//    S_WAITPKT
//else
//    S_WAIT))) {
//    override fun fn(pkt : Packet?) : Task? {
//        if (pkt != null)
//        {
//            if ((pkt?.kind)!! == Richards.K_WORK)
//                workpkts = pkt?.append_to(workpkts)
//            else
//                devpkts = pkt?.append_to(devpkts)
//        }
//        if (workpkts != null)
//        {
//            var workpkt : Packet? = workpkts
//            var count : Int = workpkt?.a1!!
//            if (count > Packet.BUFSIZE)
//            {
//                workpkts = workpkts?.link
//                return qpkt(workpkt)
//            }
//            if (devpkts != null)
//            {
//                var devpkt : Packet? = devpkts
//                devpkts = devpkts?.link
//                devpkt?.a1 = workpkt?.a2[count]
//                workpkt?.a1 = count + 1
//                return qpkt(devpkt)
//            }
//        }
//        return waitTask()
//    }
//    private var workpkts : Packet? = null
//    private var devpkts : Packet? = null
//    {
//        workpkts = devpkts = null
//    }
//}
//class IdleTask(i : Int, a1 : Int, a2 : Int) : Task(i, 0, null, S_RUN) {
//    override fun fn(pkt : Packet?) : Task? {
//        --v2
//        if (v2 == 0)
//        {
//            return hold()
//        }
//        else
//            if ((v1 and 1) == 0)
//            {
//                v1 = (v1 shr 1)
//                return release(Richards.I_DEVA)
//            }
//            else
//            {
//                v1 = (v1 shr 1) xor 53256
//                return release(Richards.I_DEVB)
//            }
//    }
//    private var v1 : Int = 0
//    private var v2 : Int = 0
//    {
//        v1 = a1
//        v2 = a2
//    }
//}
//class WorkTask(i : Int, p : Int, w : Packet?) : Task(i, p, w, ((if (w != null)
//    S_WAITPKT
//else
//    S_WAIT))) {
//    override fun fn(pkt : Packet?) : Task? {
//        if (pkt == null)
//        {
//            return waitTask()
//        }
//        else
//        {
//            handler = ((if (handler == Richards.I_HANDLERA)
//                Richards.I_HANDLERB
//            else
//                Richards.I_HANDLERA))
//            pkt?.id = handler
//            pkt?.a1 = 0
//            for (i in 0..Packet.BUFSIZE) {
//                n++
//                if (n > 26)
//                    n = 1
//                pkt?.a2[i] = 'A' + (n - 1)
//            }
//            return qpkt(pkt)
//        }
//    }
//    private var handler : Int = 0
//    private var n : Int = 0
//    {
//        handler = Richards.I_HANDLERA
//        n = 0
//    }
//}
//public class Richards() : Benchmark {
//    private var total_ms : Long = 0
//    public fun getRunTime() : Long {
//        return total_ms
//    }
//    public fun inst_main(args : Array<String?>?) : Unit {
//        System.out?.println("Richards benchmark (gibbons) starting...")
//        var startTime : Long = System.currentTimeMillis()
//        if (!run())
//            return
//        var endTime : Long = System.currentTimeMillis()
//        System.out?.println("finished.")
//        total_ms = endTime - startTime
//        System.out?.println("Total time for " + iterations + " iterations: " + (total_ms / 1000.0) + " secs")
//        System.out?.println("Average time per iteration: " + (total_ms / iterations) + " ms")
//    }
//    public fun run() : Boolean {
//        for (i in 0..iterations - 1) {
//            Task.holdCount = Task.qpktCount = 0
//            IdleTask(I_IDLE, 1, 10000)
//            var wkq : Packet? = Packet(null, 0, K_WORK)
//            wkq = Packet(wkq, 0, K_WORK)
//            WorkTask(I_WORK, 1000, wkq)
//            wkq = Packet(null, I_DEVA, K_DEV)
//            wkq = Packet(wkq, I_DEVA, K_DEV)
//            wkq = Packet(wkq, I_DEVA, K_DEV)
//            HandlerTask(I_HANDLERA, 2000, wkq)
//            wkq = Packet(null, I_DEVB, K_DEV)
//            wkq = Packet(wkq, I_DEVB, K_DEV)
//            wkq = Packet(wkq, I_DEVB, K_DEV)
//            HandlerTask(I_HANDLERB, 3000, wkq)
//            wkq = null
//            DeviceTask(I_DEVA, 4000, wkq)
//            DeviceTask(I_DEVB, 5000, wkq)
//            Task.schedule()
//            if (Task.qpktCount == 23246 && Task.holdCount == 9297)
//            else
//            {
//                System.out?.println("Incorrect results!")
//                return false
//            }
//        }
//        return true
//    }
//    class object {
//        public fun main(args : Array<String?>?) : Unit {
//            (Richards()).inst_main(args)
//        }
//        var iterations : Int = 10
//        val I_IDLE : Int = 1
//        val I_WORK : Int = 2
//        val I_HANDLERA : Int = 3
//        val I_HANDLERB : Int = 4
//        val I_DEVA : Int = 5
//        val I_DEVB : Int = 6
//        val K_DEV : Int = 1000
//        val K_WORK : Int = 1001
//    }
//}
