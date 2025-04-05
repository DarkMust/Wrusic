import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-callback',
  templateUrl: './callback.component.html',
  styleUrls: ['./callback.component.scss']
})
export class CallbackComponent implements OnInit {
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    // Get the code and state from the URL
    const code = this.route.snapshot.queryParamMap.get('code');
    const state = this.route.snapshot.queryParamMap.get('state');

    if (!code || !state) {
      this.error = 'Invalid callback URL';
      return;
    }

    // Handle the callback
    this.authService.handleCallback(code, state).subscribe(
      success => {
        if (success) {
          // Redirect to home page
          this.router.navigate(['/']);
        } else {
          this.error = 'Authentication failed';
        }
      },
      error => {
        console.error('Error handling callback:', error);
        this.error = 'Authentication failed';
      }
    );
  }
} 