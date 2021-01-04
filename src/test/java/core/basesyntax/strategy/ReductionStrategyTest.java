package core.basesyntax.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.db.Storage;
import core.basesyntax.model.Fruit;
import core.basesyntax.model.Operation;
import core.basesyntax.model.TransactionDto;
import core.basesyntax.service.impl.FruitServiceImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ReductionStrategyTest {
    public static FruitServiceImpl fruitService;
    public static List<TransactionDto> transactionDtos;
    public static Map<Operation, OperationStrategy> operationStrategyMap;

    @BeforeAll
    static void beforeAll() {
        fruitService = new FruitServiceImpl(operationStrategyMap);
        transactionDtos = new ArrayList<>();
        operationStrategyMap = new HashMap<>();
        operationStrategyMap.put(Operation.BALANCE, new AdditionalStrategy());
        operationStrategyMap.put(Operation.RETURN, new AdditionalStrategy());
        operationStrategyMap.put(Operation.SUPPLY, new AdditionalStrategy());
        operationStrategyMap.put(Operation.PURCHASE, new ReductionStrategy());
        transactionDtos.add(new TransactionDto(Operation.BALANCE,
                new Fruit("banana"), 20));
        transactionDtos.add(new TransactionDto(Operation.PURCHASE,
                new Fruit("banana"), 10));
    }

    @Test
    void subtraction_Ok() {
        fruitService.applyAllOperators(transactionDtos);
        Integer actul = Storage.fruits.size();
        assertEquals(10, actul);
    }

    @Test
    void subtractionBadData_Ok() {
        transactionDtos.add(new TransactionDto(Operation.PURCHASE,
                new Fruit("banana"), -10));
        assertThrows(RuntimeException.class, () -> fruitService.applyAllOperators(transactionDtos));
    }

    @Test
    void subtractionMoreThanWeHave_Ok() {
        transactionDtos.add(new TransactionDto(Operation.PURCHASE,
                new Fruit("banana"), 20));
        assertThrows(RuntimeException.class, () -> fruitService.applyAllOperators(transactionDtos));
    }
}
