package kg.airport.airportproject.utils;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserFlightsEntity;
import kg.airport.airportproject.entity.UserRolesEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Класс выполняющий проверки, что пользователь содержит опеределенные роли
 */
public class UserRolesUtils {


    /**
     * Метод проверяющий содержит ли пользователь роль с опеределенным названием
     * @param userRolesEntityList список ролей пользователей типа {@link UserRolesEntity}
     * @param userRoleTitle название роли пользователя
     * @return false если у пользователя нет роли с таким названием и true, если есть
     */
    public static boolean checkIfUserRolesListContainsSuchUserRoleTitle(
            List<UserRolesEntity> userRolesEntityList,
            String userRoleTitle
    ) {
        if(Objects.isNull(userRolesEntityList) || userRolesEntityList.isEmpty()) {
            throw new IllegalArgumentException("Список ролей пользователя не может быть null или пустым!");
        }
        if(Objects.isNull(userRoleTitle) || userRoleTitle.isEmpty()) {
            throw new IllegalArgumentException("Проверяемая роль пользователя не может быть null или пустой!");
        }

        for (UserRolesEntity role : userRolesEntityList) {
            if(role.getRoleTitle().equals(userRoleTitle)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIfApplicationUsersListContainsSuchUserRolesTitles(
            List<ApplicationUsersEntity> applicationUsersEntityList,
            String... searchedUserRolesTitles
    ) {
        if(Objects.isNull(applicationUsersEntityList) || applicationUsersEntityList.isEmpty()) {
            throw new IllegalArgumentException("Список пользователей не может быть null или пустым!");
        }
        if(Objects.isNull(searchedUserRolesTitles)) {
            throw new IllegalArgumentException("Список названий ролей не может быть пустым!");
        }

        List<String> usersRoleTitlesList = convertApplicationUsersListToRoleTitlesList(applicationUsersEntityList);
        List<String> searchedRoleTitlesList = new ArrayList<>(List.of(searchedUserRolesTitles));
        searchedRoleTitlesList.removeIf(usersRoleTitlesList::contains);

        return searchedRoleTitlesList.isEmpty();
    }

    public static boolean checkIfEachApplicationUserInListContainsRequiredRoles(
            List<ApplicationUsersEntity> applicationUsersEntityList,
            String... requiredUserRolesTitles
    ) {
        List<List<UserRolesEntity>> listOfUsersRoleList =
                applicationUsersEntityList
                        .stream()
                        .map(ApplicationUsersEntity::getUserRolesEntityList)
                        .collect(Collectors.toList());

        for (List<UserRolesEntity> userRolesList : listOfUsersRoleList) {
            List<String> userRolesTitles = userRolesList
                    .stream()
                    .map(UserRolesEntity::getRoleTitle)
                    .collect(Collectors.toList());

            int i = 0;
            for (; i < requiredUserRolesTitles.length; i++) {
                if(userRolesTitles.contains(requiredUserRolesTitles[i])) {
                    break;
                }
            }
            if(i == requiredUserRolesTitles.length) {
                return false;
            }
        }
        return true;
    }

    private static List<String> convertApplicationUsersListToRoleTitlesList(
            List<ApplicationUsersEntity> applicationUsersEntityList
    ) {
        return applicationUsersEntityList
                .stream()
                .map(ApplicationUsersEntity::getUserRolesEntityList)
                .flatMap(List::stream)
                .map(UserRolesEntity::getRoleTitle)
                .collect(Collectors.toList());
    }
}
