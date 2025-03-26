import { NgModule } from '@angular/core';
import { ToolbarModule } from 'primeng/toolbar';
import { ButtonModule } from 'primeng/button';
import { AccordionModule } from 'primeng/accordion';
import { MenubarModule } from 'primeng/menubar';
import { CardModule } from 'primeng/card';
import { PasswordModule } from 'primeng/password';
import { MessageModule } from 'primeng/message';
import { ToastModule } from 'primeng/toast';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { MultiSelectModule } from 'primeng/multiselect';
import { AvatarModule } from 'primeng/avatar';
import { AvatarGroupModule } from 'primeng/avatargroup';
import { CheckboxModule } from 'primeng/checkbox';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { ListboxModule } from 'primeng/listbox';
import { StepsModule } from 'primeng/steps';


@NgModule({
    exports: [
        ToolbarModule, 
        ButtonModule,
        AccordionModule,
        MenubarModule,
        CardModule,
        PasswordModule,
        MessageModule,
        ToastModule, 
        ProgressSpinnerModule,
        OverlayPanelModule,
        DropdownModule,
        InputNumberModule,
        MultiSelectModule,
        AvatarGroupModule,
        AvatarModule,
        CheckboxModule,
        TableModule,
        DialogModule,
        TagModule,
        ListboxModule,
        StepsModule
    ]
})

export class PrimeModule { }