package com.unversity.programm.exceptions;

import java.util.Set;

public class OneOrManyNotFoundException extends RuntimeException {
    private final Set<Long> missingIds;
    public OneOrManyNotFoundException(Set<Long> missingIds) {
        super("Не найдены: " + missingIds);
        this.missingIds = Set.copyOf(missingIds);
    }
    public Set<Long> getMissingIds(){
        return missingIds;
    }
}
