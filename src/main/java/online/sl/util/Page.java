package online.sl.util;


import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class Page<T> extends PageImpl<T> {

    public Page(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    @Override
    public int getNumber() {
        return super.getNumber() + 1;
    }

    @Override
    public boolean hasPrevious() {
        return super.getNumber() > 0;
    }

    @Override
    public boolean hasNext() {
        return super.getNumber() + 1 < getTotalPages();
    }

}
