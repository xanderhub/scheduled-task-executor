public enum ReduceDelay {
    BY_25_PERCENT((byte)25),
    BY_50_PERCENT((byte)50),
    BY_75_PERCENT((byte)75),
    BY_100_PERCENT((byte)100);

    public final Byte percent;

    private ReduceDelay(Byte percent) {
        this.percent = percent;
    }
}
