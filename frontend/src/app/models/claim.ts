export type ClaimStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface Claim {
  id: number;
  itemId: number;
  claimantId: number;
  message: string;
  status: ClaimStatus;
  createdAt: string;
  updatedAt: string;
}