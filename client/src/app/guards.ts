import { ActivatedRouteSnapshot, CanDeactivateFn, RouterStateSnapshot } from "@angular/router";
import { ProfileComponent } from "./components/main/user/profile.component";
import { BuildComponent } from "./components/main/build/build.component";

export const canLeavePreference: CanDeactivateFn<ProfileComponent> = (profile: ProfileComponent,
    route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    if (profile.isEditing) {
        return confirm('You have unsaved changes. Do you really want to leave?')
    }
    return true
}

// export const canLeaveBuild: CanDeactivateFn<BuildComponent> = (build: BuildComponent,
//     route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
//     if (build.isEditing) {
//         return confirm('You have unsaved changes. Do you really want to leave?')
//     }
//     return true
// }