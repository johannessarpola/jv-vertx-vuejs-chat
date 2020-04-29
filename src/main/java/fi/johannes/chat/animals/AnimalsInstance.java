package fi.johannes.chat.animals;

import fi.johannes.chat.animals.api.AnimalsRepository;
import fi.johannes.chat.animals.db.impl.AnimalsDBImpl;
import fi.johannes.chat.animals.types.Animal;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Johannes on 29.4.2020.
 */
public class AnimalsInstance {

  private static AnimalsInstance instance;
  private AnimalsRepository animalsRepository = new AnimalsDBImpl(); // TODO Injection?

  private List<Animal> animals;

  private AnimalsInstance() {
    this.animals = animalsRepository.getAnimals();
  }

  public static AnimalsInstance getInstance() {
    if (instance == null) {
      instance = new AnimalsInstance();
    }
    return instance;
  }

  public Animal getAnimal(Predicate<Animal> filter) {
    List<Animal> filteredAnimals = this.animals.parallelStream().filter(filter).collect(Collectors.toList());
    Random random = new Random();
    int ri = random.nextInt(filteredAnimals.size());
    return filteredAnimals.get(ri);
  }

}
