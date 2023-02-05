package org.goxiaogle.chainbuilder.utils;

import org.goxiaogle.chainbuilder.CheckChainBuilder;

public class ChainBuilderFactory {

    Object[] targets;

    public ChainBuilderFactory(Object... targets) {
        this.targets = targets;
    }

    public void create(CheckChainBuilder<?> builder) {

    }

}
