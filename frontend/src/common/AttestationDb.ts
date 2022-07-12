import { Connection } from "jsstore";
import workerInjector from "jsstore/dist/worker_injector";

export class AttestationDb {
  private connection;

  private attestationTable = {
    name: "AttestationModel",
    columns: {
      id: { primaryKey: true, dataType: "string" },
      attestation: { notNull: true, dataType: "object" },
    },
  };

  private db = {
    name: "attestations",
    tables: [this.attestationTable],
  };

  constructor() {
    this.connection = new Connection();
    this.connection.addPlugin(workerInjector);
  }

  async initDb() {
    await this.connection.initDb(this.db);
  }

  async close() {
    await this.connection.terminate();
  }

  async insertAttestation(
    type: string,
    email: string,
    account: string,
    attestationValue: any
  ) {
    const result = await this.connection.insert({
      into: this.attestationTable.name,
      upsert: true,
      values: [
        { id: `${type}/${email}/${account}`, attestation: attestationValue },
      ],
    });
    return result;
  }

  async getFirstAttestationByEmail(type: string, email: string) {
    const results = await this.connection.select({
      from: this.attestationTable.name,
      where: { id: { like: `${type}/${email}%` } },
    });
    return results.length ? results[0].attestation : null;
  }

  async getAttestation(type: string, email: string, account: string) {
    const results = await this.connection.select({
      from: this.attestationTable.name,
      where: { id: `${type}/${email}/${account}` },
    });
    return results.length ? results[0].attestation : null;
  }

  async deleteAttestation(account: string, type: string, email: string) {
    await this.connection.remove({
      from: this.attestationTable.name,
      where: { id: `${account}/${type}/${email}` },
    });
  }
}
