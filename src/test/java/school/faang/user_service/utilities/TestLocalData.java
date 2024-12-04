package school.faang.user_service.utilities;

public record TestLocalData(Integer pageNumber,
                            Integer pageSize,
                            Integer resultNumberRows,
                            String errorMessage,
                            boolean isLogicFilterPresent){
}