package havlak

class BasicBlockEdge(cfg: CFG, fromName: Int, toName: Int) {
    val from = cfg.createNode(fromName)
    val to = cfg.createNode(toName);

    {
        from.addOutEdge(to)
        to.addInEdge(from)
        cfg.addEdge(this)
    }
}