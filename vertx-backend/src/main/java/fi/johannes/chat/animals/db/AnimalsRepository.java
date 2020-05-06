package fi.johannes.chat.animals.db;

import fi.johannes.chat.animals.types.Animal;

import java.util.List;

/**
 * Johannes on 29.4.2020.
 */
public interface AnimalsRepository {
  List<Animal> getAnimals();
}
