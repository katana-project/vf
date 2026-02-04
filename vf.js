const isNode = typeof process !== "undefined";
const wasmPath = async () => {
    const url = new URL("./vf.wasm", import.meta.url).href;
    return isNode ? await import("node:url").then((mod) => mod.fileURLToPath(url)) : url;
};

let decompileFunc = null;
export const decompile = async (names, options) => {
    if (!decompileFunc) {
        try {
            const { load } = await import("./vf.wasm-runtime.js");
            const { exports } = await load(await wasmPath());

            decompileFunc = exports.decompile;
        } catch (e) {
            console.warn("Failed to load WASM module (non-compliant browser?), falling back to JS implementation", e);

            const { decompile: decompileJS } = await import("./vf.runtime.js");
            decompileFunc = decompileJS;
        }
    }

    return decompileFunc(Array.isArray(names) ? names : [names], options);
};
