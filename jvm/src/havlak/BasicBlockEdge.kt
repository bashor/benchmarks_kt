package havlak

public open class BasicBlockEdge(cfg : CFG?, fromName : Int, toName : Int) {
    public open fun getSrc() : BasicBlock? {
        return from
    }
    public open fun getDst() : BasicBlock? {
        return to
    }
    private var from : BasicBlock? = null
    private var to : BasicBlock? = null
    {
        from = cfg?.createNode(fromName)
        to = cfg?.createNode(toName)
        from?.addOutEdge(to)
        to?.addInEdge(from)
        cfg?.addEdge(this)
    }

}
