//package org.jetbrains.kotlin.benchmarks.from_Bench_dot_java
//
//
//import java.io.*
//import java.lang.*
//public class Bench() {
//    class object {
//        var count = 0
//        var Count = 10000
//        var Qpktcountval = 23246
//        var Holdcountval = 9297
//        var Count100 = 10000 * 100
//        var Qpktcountval100 = 2326410
//        var Holdcountval100 = 930563
//        val MAXINT = 32767
//        val BUFSIZE = 3
//        val I_IDLE = 1
//        val I_WORK = 2
//        val I_HANDLERA = 3
//        val I_HANDLERB = 4
//        val I_DEVA = 5
//        val I_DEVB = 6
//        val PKTBIT = 1
//        val WAITBIT = 2
//        val HOLDBIT = 4
//        val NOTPKTBIT = -1
//        val NOTWAITBIT = -2
//        val NOTHOLDBIT = 65531
//        val S_RUN = 0
//        val S_RUNPKT = 1
//        val S_WAIT = 2
//        val S_WAITPKT = 3
//        val S_HOLD = 4
//        val S_HOLDPKT = 5
//        val S_HOLDWAIT = 6
//        val S_HOLDWAITPKT = 7
//        val K_DEV = 1000
//        val K_WORK = 1001
//        var alphabet  = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ"
//        var tasktab = TaskTab(11)
//        var tasklist : Task? = null
//        var tcb : Task? = null
//        var taskid = 0
//        var v1 : Any? = null
//        var v2 : Any? = null
//        var qpktcount = 0
//        var holdcount = 0
//        var tracing = false
//        var layout = 0
//
//        public fun main(args : Array<String>) {
//            tracing = false
//            try
//            {
//                for (i in args.indices) {
//                    if (args[i].equals("-100"))
//                    {
//                        Count = Count100
//                        Qpktcountval = Qpktcountval100
//                        Holdcountval = Holdcountval100
//                        continue
//                    }
//                    if (args[i].equals("-t"))
//                    {
//                        tracing = true
//                        continue
//                    }
//                }
//            }
//            catch (e: NumberFormatException) {
//                System.err.println("Integer argument expected")
//            }
//            var wkq : Packet? = null
//            println("Bench mark starting")
//
//            IdleTask(I_IDLE, 0, wkq, S_RUN, 1, Count)
//
//            wkq = Packet(null, 0, K_WORK)
//            wkq = Packet(wkq, 0, K_WORK)
//            WorkTask(I_WORK, 1000, wkq, S_WAITPKT, I_HANDLERA, 0)
//
//            wkq = Packet(null, I_DEVA, K_DEV)
//            wkq = Packet(wkq, I_DEVA, K_DEV)
//            wkq = Packet(wkq, I_DEVA, K_DEV)
//
//            HandlerTask(I_HANDLERA, 2000, wkq, S_WAITPKT, null, null)
//
//            wkq = Packet(null, I_DEVB, K_DEV)
//            wkq = Packet(wkq, I_DEVB, K_DEV)
//            wkq = Packet(wkq, I_DEVB, K_DEV)
//            HandlerTask(I_HANDLERB, 3000, wkq, S_WAITPKT, null, null)
//
//            wkq = null
//            DevTask(I_DEVA, 4000, wkq, S_WAIT, 0, 0)
//            DevTask(I_DEVB, 5000, wkq, S_WAIT, 0, 0)
//
//            tcb = tasklist
//            qpktcount = 0
//            holdcount = 0
//            println("Starting")
//            layout = 0
//            schedule()
//
//            println("\nfinished\n")
//            println("qpkt count = " + qpktcount + " holdcount = " + holdcount)
//            print("These results are ")
//
//            if (qpktcount == Qpktcountval && holdcount == Holdcountval)
//            {
//                println("correct")
//            }
//            else
//            {
//                println("incorrect")
//            }
//            println("end of run")
//        }
//        fun schedule() : Unit {
//            while (tcb != null)
//            {
//                val tTcb = tcb!!
//                var pkt : Packet? = null
//                var newtcb : Task?
//                when (tTcb.state) {
//                    S_WAITPKT -> {
//                        pkt = tTcb.wkq
//                        tTcb.wkq = pkt?.link
//                        tTcb.state =
//                        if (tTcb.wkq == null)
//                            S_RUN
//                        else
//                            S_RUNPKT
//                        taskid = tTcb.id
//                        v1 = tTcb.sv1
//                        v2 = tTcb.sv2
//                        if (tracing)
//                        {
//                            trace(taskid + '0')
//                        }
//                        newtcb = tTcb.fn(pkt)
//                        tTcb.sv1 = v1
//                        tTcb.sv2 = v2
//                        tcb = newtcb
//                    }
//                    S_RUN, S_RUNPKT -> {
//                        taskid = tTcb.id
//                        v1 = tTcb.sv1
//                        v2 = tTcb.sv2
//                        if (tracing)
//                        {
//                            trace(taskid + '0')
//                        }
//                        newtcb = tTcb.fn(pkt)
//                        tTcb.sv1 = v1
//                        tTcb.sv2 = v2
//                        tcb = newtcb
//                    }
//                    S_WAIT, S_HOLD, S_HOLDPKT, S_HOLDWAIT, S_HOLDWAITPKT -> {
//                        tcb = tTcb.link
//                    }
//                    else -> {
//                        return
//                    }
//                }
//            }
//        }
//
//        public fun trace(a : Int) : Unit {
//            if (--layout <= 0) {
//                println()
//                layout = 50
//            }
//            print((a.toChar()))
//        }
//
//        public fun release(id : Int) : Task? {
//            val t = findtcb(id)
//            if (t == null)
//                return null
//            t.state = t.state and NOTHOLDBIT
//            if (t.pri > (tcb?.pri)!!)
//                return t
//            return tcb
//        }
//
//        public fun taskwait() : Task? {
//            val tTcb = tcb
//            if (tTcb != null) {
//                tTcb.state = tTcb.state or WAITBIT
//            }
//            return tcb
//        }
//
//        public fun holdself() : Task? {
//            ++holdcount
//            val tTcb = tcb
//            if (tTcb != null) {
//                tTcb.state = tTcb.state or HOLDBIT
//            }
//            return tcb?.link
//        }
//
//        fun findtcb(id : Int) : Task? {
//            var t : Task? = null
//            if (1 <= id && id <= (tasktab.upb))
//                t = tasktab.v[id]
//            if (t == null)
//                println("\nBad task id $id")
//            return t
//        }
//
//        public fun qpkt(pkt : Packet?) : Task? {
//            val t = findtcb((pkt?.id)!!)
//            if (t == null)
//                return null
//
//            qpktcount++
//            pkt?.link = null
//            pkt?.id = taskid
//
//            if (t.wkq == null)
//            {
//                t.wkq = pkt
//                t.state = t.state or PKTBIT
//                if (t.pri > (tcb?.pri)!!)
//                    return t
//            }
//            else
//            {
//                t.wkq = append(pkt, t.wkq)
//            }
//            return tcb
//        }
//
//        public fun append(pkt : Packet?, p : Packet?) : Packet? {
//            pkt?.link = null
//            if (p == null)
//                return pkt
//            var q = p
//            while (q?.link != null)
//                q = q?.link
//            q?.link = pkt
//            return p
//        }
//    }
//}
//class Packet(var link : Packet?, var id : Int, val kind : Int) {
//    var a1 : Int = 0
//    var a2 = IntArray(Bench.BUFSIZE + 1)
//}
//abstract class Task(val id : Int, val pri : Int, var wkq : Packet?, var state : Int, var sv1 : Any?, var sv2 : Any?) {
//    var link = Bench.tasklist
//    {
//        Bench.tasklist = this
//        Bench.tasktab.v[id] = this
//    }
//    abstract fun fn(packet : Packet?) : Task?
//}
//class IdleTask(id : Int, pri : Int, wkq : Packet?, state : Int, v1 : Any?, v2 : Any?) : Task(id, pri, wkq, state, v1, v2) {
//    override fun fn(packet: Packet?): Task? {
//        var x : Int = ((Bench.v2 as Int?))!! - 1
//        Bench.v2 = Integer(x)
//        if (x == 0)
//            return (Bench.holdself())
//        x = ((Bench.v1 as Int?))!!
//        if ((x and 1) == 0)
//        {
//            Bench.v1 = Integer((x shr 1) and Bench.MAXINT)
//            return (Bench.release(Bench.I_DEVA))
//        }
//        else
//        {
//            Bench.v1 = Integer(((x shr 1) and Bench.MAXINT) xor 53256)
//            return (Bench.release(Bench.I_DEVB))
//        }
//    }
//}
//class WorkTask(id : Int, pri : Int, wkq : Packet?, state : Int, v1 : Any?, v2 : Any?) : Task(id, pri, wkq, state, v1, v2) {
//    override fun fn(packet: Packet?): Task? {
//        if (packet == null)
//        {
//            return Bench.taskwait()
//        }
//        else
//        {
//            var x : Int = Bench.I_HANDLERA + Bench.I_HANDLERB - ((Bench.v1 as Int?))!!
//            Bench.v1 = Integer(x)
//            packet.id = x
//            packet.a1 = 0
//            for (i in 0..Bench.BUFSIZE) {
//                x = ((Bench.v2 as Int?))!! + 1
//                if (x > 26)
//                    x = 1
//                Bench.v2 = Integer(x)
//                packet.a2[i] = Bench.alphabet.charAt(x).toInt()
//            }
//            return Bench.qpkt(packet)
//        }
//    }
//}
//class HandlerTask(id : Int, pri : Int, wkq : Packet?, state : Int, v1 : Any?, v2 : Any?) : Task(id, pri, wkq, state, v1, v2) {
//    override fun fn(packet: Packet?): Task? {
//        if (packet != null)
//        {
//            if (packet.kind == Bench.K_WORK)
//            {
//                Bench.v1 = Bench.append(packet, (Bench.v1 as Packet?))
//            }
//            else
//            {
//                Bench.v2 = Bench.append(packet, (Bench.v2 as Packet?))
//            }
//        }
//        if (Bench.v1 != null)
//        {
//            var workpkt : Packet? = ((Bench.v1 as Packet?))
//            var count : Int = workpkt?.a1!!
//            if (count > Bench.BUFSIZE)
//            {
//                Bench.v1 = workpkt?.link
//                return Bench.qpkt(workpkt)
//            }
//            if (Bench.v2 != null)
//            {
//                var devpkt : Packet? = (Bench.v2 as Packet?)
//                Bench.v2 = devpkt?.link
//                devpkt?.a1 = workpkt?.a2!![count]
//                workpkt?.a1 = count + 1
//                return Bench.qpkt(devpkt)
//            }
//        }
//        return Bench.taskwait()
//    }
//}
//class DevTask(id : Int, pri : Int, wkq : Packet?, state : Int, v1 : Int, v2 : Int) : Task(id, pri, wkq, state, v1, v2) {
//    override fun fn(packet: Packet?) : Task? {
//        if (packet == null)
//        {
//            if (Bench.v1 == null)
//                return (Bench.taskwait())
//            val newPacket = (Bench.v1 as Packet?)
//            Bench.v1 = null
//            return Bench.qpkt(newPacket)
//        }
//        else
//        {
//            Bench.v1 = packet
//            if (Bench.tracing)
//            {
//                Bench.trace(packet.a1)
//            }
//            return Bench.holdself()
//        }
//    }
//}
//class TaskTab(val upb : Int) {
//    val v = arrayOfNulls<Task>(upb + 1);
//}
//
//fun main(args : Array<String>) = Bench.main(args)