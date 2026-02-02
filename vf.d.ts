declare module "@run-slicer/vf" {
    export type Options = Record<string, string>;

    export interface TokenCollector {
        start: (content: string) => void;
        visitClass: (start: number, length: number, declaration: boolean, name: string) => void;
        visitField: (start: number, length: number, declaration: boolean, className: string, name: string, descriptor: string) => void;
        visitMethod: (start: number, length: number, declaration: boolean, className: string, name: string, descriptor: string) => void;
        visitParameter: (start: number, length: number, declaration: boolean, className: string, methodName: string, methodDescriptor: string, index: number, name: string) => void;
        visitLocal: (start: number, length: number, declaration: boolean, className: string, methodName: string, methodDescriptor: string, index: number, name: string) => void;
        end: () => void;
    }

    export interface Config {
        source?: (name: string) => Promise<Uint8Array | null>;
        resources?: string[];
        options?: Options;
        tokenCollector?: TokenCollector;
    }

    export function decompile(names: string | string[], config?: Config): Promise<string>;
}
