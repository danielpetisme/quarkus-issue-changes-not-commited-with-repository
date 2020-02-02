package org.acme;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Fruit {

    @Id
    @GeneratedValue
    public Long id;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    public String name;

    @Override
    public String toString() {
        return "Fruit{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
