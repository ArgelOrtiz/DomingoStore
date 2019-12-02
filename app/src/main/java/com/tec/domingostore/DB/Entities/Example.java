package com.tec.domingostore.DB.Entities;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity(indexes = {
        @Index(value = "db_id DESC", unique = true)
})

public class Example {
    @Id
    private Long id;

    @NotNull
    private String db_id;
    private String title;
@Generated(hash = 2003677820)
public Example(Long id, @NotNull String db_id, String title) {
    this.id = id;
    this.db_id = db_id;
    this.title = title;
}
@Generated(hash = 646563864)
public Example() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getDb_id() {
    return this.db_id;
}
public void setDb_id(String db_id) {
    this.db_id = db_id;
}
public String getTitle() {
    return this.title;
}
public void setTitle(String title) {
    this.title = title;
}

}
