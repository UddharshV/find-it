import { Component, OnInit, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { UserService } from './services/user.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected userService = inject(UserService);

  ngOnInit() {
    this.userService.loadUsers();
  }

  onUserChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    this.userService.setCurrentUser(Number(select.value));
  }
}